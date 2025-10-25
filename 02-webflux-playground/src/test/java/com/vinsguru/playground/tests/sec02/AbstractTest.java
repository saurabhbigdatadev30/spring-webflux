package com.vinsguru.playground.tests.sec02;

import org.springframework.boot.test.context.SpringBootTest;
//  To see the R2DBC queries in the logs , we set the logging level for 'org.springframework.r2dbc' to DEBUG here.

@SpringBootTest(properties = { "sec=sec02","logging.level.org.springframework.r2dbc=DEBUG"
})
public abstract class AbstractTest {
}
