package com.shea.picture.sheapicture.manager.sharding;

import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * 图片分表算法
 * @author : Shea.
 * @since : 2026/4/26 15:01
 */
public class PictureShardingAlgorithm implements StandardShardingAlgorithm<Long> {
    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<Long> preciseShardingValue) {
        Long spaceId = preciseShardingValue.getValue();
        String logicTableName = preciseShardingValue.getLogicTableName();
        // spaceId为null表示查询所有图片
        if (spaceId == null) {
            return logicTableName;
        }
        // 根据spaceId动态生成分表名
        String realTableName = "picture_" + spaceId;
        if (collection.contains(realTableName)) {
            return realTableName;
        } else {
            return logicTableName;
        }
    }

    @Override
    public Collection<String> doSharding(Collection<String> collection, RangeShardingValue<Long> rangeShardingValue) {
        return List.of();
    }

    @Override
    public Properties getProps() {
        return null;
    }

    @Override
    public void init(Properties properties) {

    }
}
