package vn.com.gsoft.products.service;

import org.springframework.kafka.support.SendResult;
import vn.com.gsoft.products.entity.Process;
import vn.com.gsoft.products.entity.ProcessDtl;
import vn.com.gsoft.products.model.system.WrapData;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface KafkaProducer {
    SendResult<String, String> sendInternal(String topic, String payload) throws InterruptedException, ExecutionException, TimeoutException;

    SendResult<String, String> sendInternal(String topic, String key, String payload) throws InterruptedException, ExecutionException, TimeoutException;

    Process createProcess(String batchKey, String maNhaThuoc, String json, Date date, int size, Long userId) throws Exception;

    ProcessDtl createProcessDtl(Process process, WrapData data);
}


