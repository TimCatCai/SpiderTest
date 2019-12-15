package timcat;

import com.sun.org.apache.xalan.internal.xsltc.dom.StepIterator;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AllSecondNameListsProcessor implements PageProcessor {
    private Site site = Site.me();
    private Logger logger = Logger.getLogger(this.getClass().getName());
    @Override
    public void process(Page page) {
        logger.info("try agian");
        String aTagForLink = "//a[@class=\"btn btn2\"]/@href";
        //获取所有姓氏的标签
        page.putField("AllNameUrls", page.getHtml().xpath(aTagForLink).all());

    // 获取特定姓氏的标签后，抓取特定的名字
        List<String> specifySecondNames = page.getResultItems().get("AllNameUrls");

        if(specifySecondNames.size() == 0){
            page.setSkip(true);
        }else{
            String prefix = "http:";
            String firstSecondNameListPageUrl = prefix + specifySecondNames.get(0);
            page.addTargetRequest(firstSecondNameListPageUrl);
            logger.info("Process end");
        }

//        String firstSecondNameAllList = getOneSecondNameAllLists(firstSecondNameListPageUrl, page);
//        page.putField("FirstSecondName", firstSecondNameAllList);
    }

    @Override
    public Site getSite() {
        return site;
    }


    /**
     * 每一个姓氏的所有名字分布在不同页面中，页面的格式是: http://xxxx.resgain.net/name_list_{0}.html
     * 其中{0}是一个递增的序列，从1开始，递增到<title>标签为 ”出错了...“ 结束
     * 每个姓名页面中，获取属性为name="keywords" 的meta标签中的content属性值
     * @param startUrl 一个姓的所有名字的页面的开始URL：格式为：http://xxxx.resgain.net/name_list.html
     *                 必须是完整的字符串：前面获取的是去掉"http:"的url,必须加上后传入
     * @param page     Webmagic 的页面对象引用
     * @return  包含一个姓氏所有名字的字符串
     */
    private String getOneSecondNameAllLists(String startUrl, Page page) {
        StringBuilder namesBuilder = new StringBuilder();
        String titleTagText = "//title/text()";
        String errorPageTitleText = "出错了...";
        String titleText;
        String nextUrl;
        String namesString;

        for (int i = 1; true; i++) {
            nextUrl = getNextNameListUrl(startUrl, i);
            logger.info("nextUrl: " + nextUrl);
            page.addTargetRequest(nextUrl);
            titleText = StringUtils.trim(page.getHtml().xpath(titleTagText).get());
            logger.info("titleText: " + titleText);
            // 如果到了出错页面，退出循环
            if (StringUtils.equals(titleText, errorPageTitleText)) {
                break;
            }
            // 如果获取到有效页面，从该页面中获取名字字符串
            namesString = getOneSecondNameList(nextUrl, page);
            // 拼接所有名字字符串
            namesBuilder.append(namesString);
            logger.info(namesString);
        }
        return namesBuilder.toString();
    }

    /**
     * 获取一个姓名页面中，属性为name="keywords" 的meta标签中的content属性值
     * @param url  必须是完整的字符串：前面获取的是去掉"http:"的url,必须加上后传入
     * @param page Webmagic 页面对象的引用
     * @return 一个名字页面中，包含所有名字的字符串
     */
    private String getOneSecondNameList(String url, Page page) {
        page.addTargetRequest(url);
        // 获取名字页面的meta标签的content属性
        String metaTagForName = "//meta[@name=\"keywords\"]/@content";
        String names = page.getHtml().xpath(metaTagForName).get();
        // 去掉前面无用的字符后就返回名字字符串
        return processNamesString(names);
    }

    /**
     * 获取到的姓名字符串前面中有：”赵姓名字大全,赵姓男孩名字大全,赵姓女孩名字大全,赵姓取名:赵婧文,“的干扰字符，
     * 除去他们
     *
     * @param names 所要处理的名字字符串
     * @return 除去前面干扰字符的字符串
     */
    private String processNamesString(String names) {
        int deletePrefixNum = 4;
        // 截取前面deletePrefixNum个逗号的内容，获取第deletePrefixNum + 1个字符串即为所要的字符串
        String[] resultList = StringUtils.split(names, ",", deletePrefixNum + 1);
        return resultList.length >= deletePrefixNum + 1 ? resultList[deletePrefixNum] : null;
    }


    private String getNextNameListUrl(String startUrl, int index){
        String suffix = ".html";
        // 在 suffix之前插入
        int insetPosition = suffix.length();
        String prefixBeforeIndex = "_";
        return startUrl.substring(0, startUrl.length() - insetPosition) + prefixBeforeIndex + index + suffix;
    }

}
