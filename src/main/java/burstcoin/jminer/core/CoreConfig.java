/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 by luxe - https://github.com/de-luxe - BURST-LUXE-RED2-G6JW-H4HG5
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package burstcoin.jminer.core;

import burstcoin.jminer.core.checker.util.LowestNonceFinder;
import burstcoin.jminer.core.checker.util.OCLChecker;
import burstcoin.jminer.core.checker.util.ShaLibChecker;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * The type Core config.
 */
@Configuration
@ComponentScan(basePackages = {"burstcoin.jminer.core"})
public class CoreConfig
{
  private static final Logger logger = LoggerFactory.getLogger(CoreConfig.class);

  @Bean(name = "readerPool")
  public ThreadPoolTaskExecutor readerPool()
  {
    ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
    pool.setThreadPriority(Thread.NORM_PRIORITY);
    // false-> triggers interrupt exception at shutdown
    pool.setWaitForTasksToCompleteOnShutdown(true);
    pool.initialize();
    return pool;
  }

  @Bean(name = "networkPool")
  public SimpleAsyncTaskExecutor networkPool()
  {
    SimpleAsyncTaskExecutor pool = new SimpleAsyncTaskExecutor();
    pool.setThreadPriority(Thread.NORM_PRIORITY + 1);
    return pool;
  }

  @Bean
  public HttpClient httpClient()
  {
    HttpClient client = new HttpClient(new SslContextFactory(true));
    try
    {
      client.start();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    return client;
  }

  @Bean
  public ObjectMapper objectMapper()
  {
    return new ObjectMapper();
  }

  @Bean
  public LowestNonceFinder getLowestNonceFinder() {
    if (CoreProperties.getUseOpenCl()) {
      return new OCLChecker();
    } else {
      try {
        return new ShaLibChecker();
      } catch (Exception | LinkageError e) {
        logger.warn("Error initialising Shabal Library, falling back to OpenCL", e);
        return new OCLChecker();
      }
    }
  }
}
