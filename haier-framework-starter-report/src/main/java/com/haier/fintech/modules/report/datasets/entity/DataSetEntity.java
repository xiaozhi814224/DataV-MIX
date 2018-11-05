package com.haier.fintech.modules.report.datasets.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import lombok.Data;

@Data
@TableName("tb_data_set_config")
public class DataSetEntity {
	@TableId
	private Long id;
	private Long dataSourceId;//数据源Id
	private String name;//数据集名称
	private String sql;//查询sql
	private Integer actTime;//实时时间  1、1小时  2、12 小时  3、24小时
	private Integer loadType;//加载方式  1 从缓存加载  2 不从缓存加载
	private Date createTime;//创建时间
	private Date updateTime;//更新时间
	private Long createUser;//创建者
	private Long updateUser;//更新者
	private Long configId;//参数ID
}
