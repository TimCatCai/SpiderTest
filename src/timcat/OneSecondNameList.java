package timcat;

import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.net.BindException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class OneSecondNameList implements PageProcessor {
    private Site site = Site.me().setRetryTimes(5);
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private String baseUrl;
    // 原子存取的页面序号
    private AtomicInteger index;
    private StringBuilder result;
    /**
     *
     * @param baseUrl 必须是完整的字符串：前面获取的是去掉"http:"的url,必须加上后传入
     */
    public OneSecondNameList(String baseUrl, StringBuilder builder){
        this.baseUrl = baseUrl;
        // 起始index默认为0
        index = new AtomicInteger(0);
        result = builder;
    }
    @Override
    public void process(Page page) {
        String names = getOneSecondNameList(page);
        int currentIndex;
        if(names == null){
             logger.info("names is null");
            page.setSkip(true);
        }else{
            // 这里不知道会不会有线程安全问题
            result.append(names);
            currentIndex = getIndex();
            page.putField("List" + currentIndex, names);
            String nextUrl = getNextNameListUrl(baseUrl, currentIndex + 1);
//            logger.info("nextUrl: " + nextUrl);
            page.addTargetRequest(nextUrl);
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    /**
     * 获取一个姓名页面中，属性为name="keywords" 的meta标签中的content属性值

     * @param page Webmagic 页面对象的引用
     * @return 一个名字页面中，包含所有名字的字符串
     */
    private String getOneSecondNameList(Page page) {
        // 获取名字页面的meta标签的content属性
        String metaTagForName = "//meta[@name=\"description\"]/@content";
        String names = page.getHtml().xpath(metaTagForName).get();
//        logger.info("original names String" + names);
        // 去掉前面无用的字符后就返回名字字符串
        return processNamesString(names);
    }

    /**
     * 获取到的姓名字符串前面中有：”赵姓名字大全,赵姓男孩名字大全,赵姓女孩名字大全,赵姓取名:赵婧文,“的干扰字符，
     * 除去他们
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
        return StringUtils.substring(startUrl,0, startUrl.length() - insetPosition) + prefixBeforeIndex + index + suffix;
    }

    private int getIndex(){
        return index.incrementAndGet();
    }
}
