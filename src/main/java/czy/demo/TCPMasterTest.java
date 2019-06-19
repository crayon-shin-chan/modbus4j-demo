package czy.demo;

import com.serotonin.modbus4j.BatchRead;
import com.serotonin.modbus4j.BatchResults;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.base.ModbusUtils;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.code.RegisterRange;
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

    private static final BatchRead<String> batch1 = new BatchRead<>();
    private static final BatchRead<String> batch2 = new BatchRead<>();

    static {
        for(int i=0;i<10;i++){
            batch1.addLocator(Constant.SLAVE_ID_1+"_"+ RegisterRange.COIL_STATUS+"_"+i,BaseLocator.coilStatus(Constant.SLAVE_ID_1,i));
            batch1.addLocator(Constant.SLAVE_ID_1+"_"+ RegisterRange.INPUT_STATUS+"_"+i,BaseLocator.inputStatus(Constant.SLAVE_ID_1,i));
            batch1.addLocator(Constant.SLAVE_ID_1+"_"+ RegisterRange.HOLDING_REGISTER+"_"+i,BaseLocator.holdingRegister(Constant.SLAVE_ID_1,i, DataType.TWO_BYTE_INT_SIGNED));
            batch1.addLocator(Constant.SLAVE_ID_1+"_"+ RegisterRange.INPUT_REGISTER+"_"+i,BaseLocator.inputRegister(Constant.SLAVE_ID_1,i, DataType.TWO_BYTE_INT_SIGNED));
            batch2.addLocator(Constant.SLAVE_ID_2+"_"+ RegisterRange.COIL_STATUS+"_"+i,BaseLocator.coilStatus(Constant.SLAVE_ID_1,i));
            batch2.addLocator(Constant.SLAVE_ID_2+"_"+ RegisterRange.INPUT_STATUS+"_"+i,BaseLocator.inputStatus(Constant.SLAVE_ID_1,i));
            batch2.addLocator(Constant.SLAVE_ID_2+"_"+ RegisterRange.HOLDING_REGISTER+"_"+i,BaseLocator.holdingRegister(Constant.SLAVE_ID_1,i, DataType.TWO_BYTE_INT_SIGNED));
            batch2.addLocator(Constant.SLAVE_ID_2+"_"+ RegisterRange.INPUT_REGISTER+"_"+i,BaseLocator.inputRegister(Constant.SLAVE_ID_1,i, DataType.TWO_BYTE_INT_SIGNED));
        }
    }

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

        get.schedule(new TimerTask() {
            @Override
            public void run() {
                try{
                    BatchResults<String> results1 = master.send(batch1);
                    BatchResults<String> results2 = master.send(batch2);
                    logger.info("slave id 1 读取结果为："+results1);
                    logger.info("slave id 2 读取结果为："+results2);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        },1000,10000);


    }

}
