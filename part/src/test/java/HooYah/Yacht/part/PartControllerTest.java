package HooYah.Yacht.part;

import HooYah.Yacht.part.controller.PartController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PartControllerTest {

    private PartController partController;



    @BeforeEach
    public void init() {

    }

    @Test
    public void getPartListByYachtTest() {
        // repo, redis mocking
    }

}
