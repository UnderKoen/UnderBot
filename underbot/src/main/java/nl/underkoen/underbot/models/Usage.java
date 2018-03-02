package nl.underkoen.underbot.models;

import com.sun.management.OperatingSystemMXBean;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import nl.underkoen.underbot.utils.NumberUtil;

import java.lang.management.ManagementFactory;

/**
 * Created by Under_Koen on 11/01/2018.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Usage {
    @Getter
    private double totalCpuUsage;

    @Getter
    private double processCpuUsage;

    @Getter
    private double totalAssignedRam;

    @Getter
    private double processRamUsage;

    public static Usage getCurrentUsage() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        Runtime runtime = Runtime.getRuntime();
        double totalCpuUsage = NumberUtil.round(osBean.getSystemCpuLoad() * 100, 1);
        double processCpuUsage = NumberUtil.round(osBean.getProcessCpuLoad() * 100, 1);

        double totalRam = NumberUtil.round(runtime.maxMemory() / 1024.0 / 1024.0, 1);
        double processRamUsage = NumberUtil.round((runtime.totalMemory() - runtime.freeMemory()) / 1024.0 / 1024.0, 1);
        return new Usage(totalCpuUsage, processCpuUsage, totalRam, processRamUsage);
    }
}
