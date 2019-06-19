package czy.demo;

import com.serotonin.modbus4j.BatchRead;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.base.ModbusUtils;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.locator.BaseLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

/* modbus-tcp客户端测试 */
public class TCPMasterTest {

    private static final Logger logger = LoggerFactory.getLogger(TCPMasterTest.class);

    private static final Timer get = new Timer("get");

    public static void main(String[] args)throws Exception{

        /* 服务端域名、端口 */
        IpParameters ipParameters = new IpParameters();
        ipParameters.setHost("127.0.0.1");
        ipParameters.setPort(ModbusUtils.TCP_PORT);
        ipParameters.setEncapsulated(false);

        /* 创建客户端 */
        ModbusFactory factory = new ModbusFactory();
        ModbusMaster master = factory.createTcpMaster(ipParameters,true);

        master.setTimeout(2000);
        master.setRetries(0);
        master.init();

        BatchRead<Integer> batch = new BatchRead<Integer>();
        get.schedule(new TimerTask() {
            @Override
            public void run() {
                for(int i=1;i<10;i++){
                    try{
                        Boolean value = master.getValue(BaseLocator.coilStatus(Constant.slaveId,i));
                        logger.info("offset："+i+"，value："+value);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        },1000,1000);


    }

}
