package com.haier.fintech.modules.report.datasources.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import lombok.Data;
@Data
@TableName("tb_data_source_config")
public class DataSourceEntity {
	@TableId
	private Long id;
	private String name;//数据源名称
	private Integer type;//数据库类型  1 MySQL 2 Oracle 3 MongoDB
	private String accessUrl;//访问URL
	private String root;//登录名
	private String password;//登录密码
	private String ramark;//备注信息
	private Date createTime;//创建时间
	private Date updateTime;//更新时间
	private Long createUser;//创建者
	private Long updateUser;//更新者
}
