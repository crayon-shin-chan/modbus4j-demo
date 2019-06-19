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
    private static final BasicProcessImage processImage1 = new BasicProcessImage(Constant.SLAVE_ID_1);
    private static final BasicProcessImage processImage2 = new BasicProcessImage(Constant.SLAVE_ID_2);

    private static final Timer update = new Timer("update");

    private static final TimerTask task = new TimerTask() {
        @Override
        public void run() {
            int current = (int)System.currentTimeMillis()%100;
            for(int i=0;i<10;i++){
                processImage1.setCoil(i,current%2==0);
                processImage2.setCoil(i,current%2!=0);
                processImage1.setInput(i,current%3==0);
                processImage2.setInput(i,current%3!=0);
                processImage1.setHoldingRegister(i,(short)(current-i));
                processImage2.setHoldingRegister(i,(short)(current+i));
                processImage1.setInputRegister(i,(short)(current+i));
                processImage2.setInputRegister(i,(short)(current-i));
            }
        }
    };

    public static void main(String[] args)throws Exception{

        /* 创建服务端 */
        ModbusFactory factory = new ModbusFactory();
        ModbusSlaveSet slave = factory.createTcpSlave(false);

        /* 添加进程镜像，并启动 */
        slave.addProcessImage(processImage1);
        slave.addProcessImage(processImage2);

        /* 开始数据写出调度任务 */
        update.schedule(task, 1000, 5001);
        logger.info("服务端已启动");
        slave.start();


    }

}
