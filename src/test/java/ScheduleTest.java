import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class ScheduleTest {
    private WebDriver driver;

    @BeforeClass
    public void setUpClass() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
    }

    // список преподавателей
    @DataProvider(name = "professors")
    public Object[][] professorsData() {
        return new Object[][]{
                {"Анищенко Юлия Владимировна"},
                {"Шабалдина Наталия Владимировна"},
                {"Замятин Александр Владимирович"}
        };
    }

    // список факультетов и групп
    @DataProvider(name = "groupsData")
    public Object[][] groupsData() {
        return new Object[][]{
                {"Институт прикладной математики и компьютерных наук", "932426"},
                {"Институт прикладной математики и компьютерных наук", "932427"},
                {"Механико-математический факультет", "042411"},
                {"Механико-математический факультет", "042412"}
        };
    }

    @Test(description = "Проверка перехода в раздел групп")
    public void testGroupsLinkClickable() {
        driver.get("https://intime.tsu.ru/"); // Переход на главную страницу
        WebElement mainLink = driver.findElement(By.linkText("Группы"));
        Assert.assertTrue(mainLink.isDisplayed() && mainLink.isEnabled());
        mainLink.click();
        Assert.assertEquals(driver.getCurrentUrl(), "https://intime.tsu.ru/groups");
    }

    @Test(description = "Проверка перехода в раздел преподавателей")
    public void testProfessorsLinkClickable() {
        driver.get("https://intime.tsu.ru/"); // Переход на главную страницу
        WebElement mainLink = driver.findElement(By.linkText("Преподаватели"));
        Assert.assertTrue(mainLink.isDisplayed() && mainLink.isEnabled());
        mainLink.click();
        Assert.assertEquals(driver.getCurrentUrl(), "https://intime.tsu.ru/professors");
    }

    @Test(dataProvider = "professors", description = "Проверка отображения расписания для разных преподавателей")
    public void testProfessorSchedule(String professorName) {
        driver.get("https://intime.tsu.ru/professors"); // Переход на страницу преподавателей

        // Найти выпадающий список и ввести имя преподавателя
        WebElement professorSelect = driver.findElement(By.className("ant-select-selection-search-input"));
        professorSelect.click();
        professorSelect.sendKeys(professorName);
        professorSelect.sendKeys(Keys.ENTER);

        // Найти и нажать кнопку для перехода к расписанию
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement showScheduleButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Показать расписание')]")));
        showScheduleButton.click();

        // Проверить, что заголовок h2 содержит имя преподавателя
        WebElement header = driver.findElement(By.tagName("h2"));
        Assert.assertTrue(header.getText().contains(professorName), "Заголовок не содержит имя преподавателя");
    }

    @Test(dataProvider = "groupsData", description = "Проверка отображения расписания для групп")
    public void testGroupsSchedule(String facultyName, String groupNumber) {

        driver.get("https://intime.tsu.ru/groups"); // Переход на страницу преподавателей

        // Выбор факультета
        WebElement facultySelect = driver.findElement(By.xpath("(//input)[1]"));
        facultySelect.click();
        facultySelect.sendKeys(facultyName);
        facultySelect.sendKeys(Keys.ENTER);

        // Выбор группы после ожидания
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement groupSelect = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//input)[2]")));
        groupSelect.click();
        groupSelect.sendKeys(groupNumber);
        groupSelect.sendKeys(Keys.ENTER);

        // Найти и нажать кнопку для перехода к расписанию
        WebDriverWait waitSchedule = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement showScheduleButton = waitSchedule.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Показать расписание')]")));
        showScheduleButton.click();

        // Проверка, что заголовок h2 содержит номер группы
        WebElement header = driver.findElement(By.tagName("h2"));
        Assert.assertTrue(header.getText().contains(groupNumber), "Заголовок не содержит номер группы: " + groupNumber);
    }

    @Test(description = "Проверка наличия предметов на неделе в аудитории")
    public void testScheduleContainsLessons() {
        // Указываем путь для аудитории, чтобы открыть страницу
        String scheduleUrlPath = "https://intime.tsu.ru/schedule/audience/ec72b3a2-c010-11ea-8117-005056bc52bb?name=235%20(2)%20Компьютерный%20класс";
        driver.get(scheduleUrlPath);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // Ожидание загрузки таблицы расписания
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("schedule-table")));
        // Поиск всех элементов span внутри расписания, то есть поиск предметов и выгрузка в список
        List<WebElement> lessons = driver.findElements(By.xpath("//span[contains(@class, 'ant-tag')]"));
        // Проверим, что хотя бы один элемент найден
        Assert.assertFalse(lessons.isEmpty(), "Нет предметов на неделе.");
        // Проверим что на неделе ровно 5 предметов
        Assert.assertEquals(lessons.size(), 5);
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}
