package crawler;

import java.io.IOException;

public interface ICrawler {
    //    Hàm để lưu dữ liệu vào file
    public void saveData() throws IOException;

    // Hàm để thực hiện crawl và save dữ liệu vào file
    public void crawlAndSave() throws IOException;
}
