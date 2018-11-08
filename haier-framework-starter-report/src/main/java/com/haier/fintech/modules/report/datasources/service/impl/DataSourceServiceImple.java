package com.haier.fintech.modules.report.datasources.service.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.haier.fintech.common.utils.PageUtils;
import com.haier.fintech.common.utils.Query;
import com.haier.fintech.datasources.DynamicDataSource;
import com.haier.fintech.modules.report.datasources.dao.DataSourceDao;
import com.haier.fintech.modules.report.datasources.entity.DataSourceEntity;
import com.haier.fintech.modules.report.datasources.service.DataSourceService;

@Service("dataSourceService")
public class DataSourceServiceImple extends ServiceImpl<DataSourceDao, DataSourceEntity> implements DataSourceService {
	@Resource
	DynamicDataSource dynamicDataSource;
	
	@PostConstruct
	public void init() {
		loadDynamicDataSource();
	}
	
	
	@Override
	public PageUtils queryPage(HashMap<String, Object> params) {
		
		Page<DataSourceEntity> page = null;
		EntityWrapper<DataSourceEntity> eWrapper = new EntityWrapper<DataSourceEntity>();
	    String timeStr = (String) params.get("createTime");
	    String type =(String)params.get("type");
	    String name=(String)params.get("name");
	    if(StringUtils.isNotBlank(type)) {
	    	eWrapper.eq("type", type);
	    }
	    if(StringUtils.isNotBlank(timeStr)) {
	    	String [] createTime = timeStr.split(",");
	    	eWrapper.between("create_time", createTime[0], createTime[1]);
	    }
	    if(StringUtils.isNotBlank(name)) {
	    	eWrapper.eq("name", name);
	    }
		page = this.selectPage(new Query<DataSourceEntity>(params).getPage(),eWrapper); 
		return new PageUtils(page);
	}
	/**
	 * 新增数据源
	 */
	@Override
	@Transactional
	public void save(DataSourceEntity dataSourceEntity,Long userId) {
		dataSourceEntity.setCreateTime(new Date());
		dataSourceEntity.setUpdateTime(new Date());
		dataSourceEntity.setCreateUser(userId);
		dataSourceEntity.setUpdateUser(userId);
		baseMapper.insert(dataSourceEntity);
		Integer type = dataSourceEntity.getType();
		if(type == 3) {//非关系型数据库，MongoDB
			
		}else {//关系型数据库
			Map<String,String> map = loadDataSource(dataSourceEntity);
			dynamicDataSource.addDataSource("datav" + dataSourceEntity.getId(), map);
		}
		
	}
	/**
	 * 查询数据源信息
	 */
	@Override
	public DataSourceEntity getInfoById(Long id) {
		DataSourceEntity entity = new DataSourceEntity();
		entity.setId(id);
		return baseMapper.selectOne(entity);
	}
	/**
	 * 更新数据源
	 */
	@Override
	@Transactional
	public void updateDataSource(DataSourceEntity dataSourceEntity, Long userId) {
		dataSourceEntity.setUpdateTime(new Date());
		dataSourceEntity.setUpdateUser(userId);
		baseMapper.updateById(dataSourceEntity);
		Map<String,String> map = loadDataSource(dataSourceEntity);
		//DynamicDataSource.DbContextHolder.setDataSource("datav" + dataSourceEntity.getId());
	}
	//获取数据源
	public Map<String,String> loadDataSource(DataSourceEntity dataSourceEntity) {
		Map<String,String> jdbcMap = new HashMap<String,String>();
		Integer type = dataSourceEntity.getType();
		String driverClassName = "";
		if(type == 1) {
			driverClassName = "com.mysql.jdbc.Driver";
		}else if(type == 2 ) {
			driverClassName = "oracle.jdbc.driver.OracleDriver";
		}
		String url = dataSourceEntity.getAccessUrl();
		String userName = dataSourceEntity.getRoot();
		String password = dataSourceEntity.getPassword();
		jdbcMap.put("jdbcUrl", url);
		jdbcMap.put("driverClassName", driverClassName);
		jdbcMap.put("username", userName);
		jdbcMap.put("password", password);
		return jdbcMap;
	}
	@Override
	public void deleteByIds(Long[] ids) {
		List<Long> list = Arrays.asList(ids);
		baseMapper.deleteBatchIds(list);
	}
	/**
	 * 获取数据源列表
	 */
	@Override
	public List<DataSourceEntity> getDataSourceList() {
		EntityWrapper<DataSourceEntity> wrapper = new EntityWrapper<DataSourceEntity>();
		return baseMapper.selectList(wrapper);
	}
	
	@Override
	public void loadDynamicDataSource() {
		List<DataSourceEntity> list = this.getDataSourceList();
		Map<String,Map<String,String>> dataSourceInfo = new HashMap<String,Map<String,String>>();
		for (DataSourceEntity dataSourceEntity : list) {
			Map<String,String> jdbcMap = new HashMap<String,String>();
			Integer type = dataSourceEntity.getType();
			Long dataSourceId = dataSourceEntity.getId();
			String driverClassName = "";
			if(type == 3) {
				
			}else {
				if(type == 1) {
					driverClassName = "com.mysql.jdbc.Driver";
				}else if(type == 2 ) {
					driverClassName = "oracle.jdbc.driver.OracleDriver";
				}
				String url = dataSourceEntity.getAccessUrl();
				String userName = dataSourceEntity.getRoot();
				String password = dataSourceEntity.getPassword();
				jdbcMap.put("jdbcUrl", url);
				jdbcMap.put("driverClassName", driverClassName);
				jdbcMap.put("username", userName);
				jdbcMap.put("password", password);
				dataSourceInfo.put(dataSourceEntity.getName(), jdbcMap);
				dynamicDataSource.addDataSource("datav" + dataSourceId, jdbcMap);
			}
			
		}
	}
	//查看数据源是否连接成功
	@Override
	public Connection checkConnect(DataSourceEntity dataSourceEntity){
		int type = dataSourceEntity.getType();
		String driverClassName = "";
		if(type == 1) {//mysql
			driverClassName = "com.mysql.jdbc.Driver";
		}else if(type == 2) {//oracle
			driverClassName = "oracle.jdbc.driver.OracleDriver";
		}
		String url = dataSourceEntity.getAccessUrl();
		String userName = dataSourceEntity.getRoot();
		String password = dataSourceEntity.getPassword();
		Connection con = null;
		if(StringUtils.isNotBlank(driverClassName) && StringUtils.isNotBlank(url) && StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
			con = getConn(driverClassName, url, userName, password);
		}
		return con;
	}
	private static Connection getConn(String driverName,String url,String userName,String password) {
	    Connection conn = null;
	    try {
	        Class.forName(driverName); //classLoader,加载对应驱动
	        conn = (Connection) DriverManager.getConnection(url, userName, password);
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return conn;
	}

//	//测试MongoDB数据库连接
//	@Override
//	public MongoDatabase checkMongoDbConnect(DataSourceEntity dataSourceEntity) {
//		MongoDatabase mongoDatabase = null;
//		 try {  
//			 	String accessUrl = dataSourceEntity.getAccessUrl();
//			 	Map<String,String> map = handelMongoAccessUrl(accessUrl);
//	            ServerAddress serverAddress = new ServerAddress(map.get("ip"),Integer.parseInt(map.get("port"))); 
//	            List<ServerAddress> addrs = new ArrayList<ServerAddress>();  
//	            addrs.add(serverAddress); 
//	            MongoCredential credential = MongoCredential.createScramSha1Credential(dataSourceEntity.getRoot(), map.get("dbName"), dataSourceEntity.getPassword().toCharArray());  
//	            List<MongoCredential> credentials = new ArrayList<MongoCredential>();  
//	            credentials.add(credential);  
//	            //通过连接认证获取MongoDB连接  
//				MongoClient mongoClient = new MongoClient(addrs,credentials);  
//	              
//	            //连接到数据库  
//	           mongoDatabase = mongoClient.getDatabase(map.get("dbName"));  
//	        } catch (Exception e) {  
//	            System.err.println( e.getClass().getName() + ": " + e.getMessage() );  
//	        }  
//		return mongoDatabase;
//	}
	//处理mongo URL
	public Map<String,String> handelMongoAccessUrl(String accessUrl) throws Exception{
		Map<String,String> map = new HashMap<String,String>();
		String[] strArr = null;
		String[] str = null;
		strArr = accessUrl.split("//");
		if(strArr.length < 2) {
			throw new Exception("访问URL有误");
		}else {
			str = strArr[1].split("/");
			if(str.length < 2) {
				throw new Exception("访问URL有误");
			}else {
				String ip = str[0].split(":")[0];
				String port = str[0].split(":")[1];
				String dbName = str[1];
				map.put("ip", ip);
				map.put("port", port);
				map.put("dbName", dbName);
			}
		}
	 	return map;
	}
}
