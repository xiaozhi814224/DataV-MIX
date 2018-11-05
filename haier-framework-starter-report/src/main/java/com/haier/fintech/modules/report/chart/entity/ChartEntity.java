package com.haier.fintech.modules.report.chart.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import lombok.Data;
@Data
@TableName("tb_chart_config")
public class ChartEntity {
	@TableId
	private Long id;
	private Long dataSetId;//数据集Id
	private String name;//图表名称
	private String type;//图表类型     1 折线图 2 柱状图 3 条形图 4 饼形图 5 地图
	private String colum;//列维
	private String row;//行维
	private String filter;//过滤条件
	private String index;//指标
	private String remark;//备注
	private String deptId;//部门Id
	private Date createTime;//创建时间
	private Date updateTime;//更新时间
	private Long createUser;//创建者
	private Long updateUser;//更新者 
	private Integer loadType;//加载方式
	private String indexCloums;//指标列
	private String reportNum;//图表编号
	private String serviceName;//图表实现类名称	
	private Long configId;//参数ID

}
