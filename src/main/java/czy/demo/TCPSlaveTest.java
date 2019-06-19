package czy.demo;

import com.serotonin.modbus4j.BasicProcessImage;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusSlaveSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

/* 服务端测试，模拟一个modbus-tcp服务端 */
public class TCPSlaveTest {

    private static final Logger logger = LoggerFactory.getLogger(TCPSlaveTest.class);

    /* 服务端数据管理 */
    private static final BasicProcessImage processImage = new BasicProcessImage(Constant.slaveId);

    private static final Timer update = new Timer("update");

    /* 更新数据任务 */
    private static final TimerTask task = new TimerTask() {
        @Override
        public void run() {
            for(int i=0;i<10;i++){
                logger.info("设置输入，offset："+i+"，value："+(i%5==0));
                processImage.setCoil(i,i%5==0);
            }
        }
    };

    public static void main(String[] args)throws Exception{

        /* 创建服务端 */
        ModbusFactory factory = new ModbusFactory();
        ModbusSlaveSet slave = factory.createTcpSlave(false);

        /* 添加进程镜像，并启动 */
        slave.addProcessImage(processImage);
        /* 开始数据写出调度任务 */
        update.schedule(task, 1000, 1000);
        logger.info("服务端已启动");
        slave.start();


    }

}
