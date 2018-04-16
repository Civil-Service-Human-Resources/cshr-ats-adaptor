package uk.gov.cshr.atsadaptor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cshr.atsadaptor.service.cshr.DepartmentsApplicationListener;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AtsAdaptorApplicationTests {

    @MockBean
    private DepartmentsApplicationListener listener;

    @Test
    public void contextLoads() {
    }

}
