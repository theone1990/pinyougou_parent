import org.csource.fastdfs.*;
import org.junit.Test;

public class TestFastDFS {

    //测试上传图片
    @Test
    public void testUpload() throws Exception{
        //1.加载配置文件
        ClientGlobal.init("F:\\IdeaProject\\pinyougou_parent\\pinyougou_shop_web\\src\\main\\resources\\config\\fastdfs_client.conf");
        //2.创建trackerClient对象
        TrackerClient trackerClient = new TrackerClient();
        //3.通过客户端获取服务端对象 trackerServer
        TrackerServer trackerServer = trackerClient.getConnection();
        //4.定义一个storageServer变量
        StorageServer storageServer = null;
        //5.创建一个storageClient
        StorageClient storageClient = new StorageClient(trackerServer,storageServer);
        //6.使用方法上传图片, 下载图片
        String[] strings = storageClient.upload_file("D:\\Pictures\\mm.jpg", "jpg", null);
        for (String string : strings) {
            System.out.println(string);
        }
    }

    //测试删除图片
    @Test
    public void delete(){
        try {
            ClientGlobal.init("F:\\IdeaProject\\pinyougou_parent\\pinyougou_shop_web\\src\\main\\resources\\config\\fastdfs_client.conf");
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageServer storageServer = null;
            StorageClient storageClient = new StorageClient(trackerServer,storageServer);
            int i = storageClient.delete_file("group1", "M00/00/00/wKgZhVt6xJOAFqCFAAO0Cx4-A-I120.jpg");
            System.out.println(i==0 ? "删除成功" : "删除失败"+i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
