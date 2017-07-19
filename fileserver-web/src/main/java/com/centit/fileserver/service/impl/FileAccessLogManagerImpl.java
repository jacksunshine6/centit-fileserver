package com.centit.fileserver.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.centit.fileserver.dao.FileAccessLogDao;
import com.centit.fileserver.po.FileAccessLog;
import com.centit.fileserver.service.FileAccessLogManager;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.hibernate.dao.DatabaseOptUtils;
import com.centit.framework.hibernate.dao.SysDaoOptUtils;
import com.centit.framework.hibernate.service.BaseEntityManagerImpl;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.database.QueryAndNamedParams;
import com.centit.support.database.QueryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class FileAccessLogManagerImpl extends BaseEntityManagerImpl<FileAccessLog, String, FileAccessLogDao> 
 implements FileAccessLogManager {

	@Resource(name ="fileAccessLogDao")
	@NotNull
	@Override
	protected void setBaseDao(FileAccessLogDao baseDao) {
		super.baseDao = baseDao;
	}
	
	@Override
	public String saveNewAccessLog(FileAccessLog fileAccessLog) {
		if(fileAccessLog.getTokenExpireTime()==null)
			fileAccessLog.setTokenExpireTime(DatetimeOpt.addHours(DatetimeOpt.currentUtilDate(),1));
		return baseDao.saveNewObject(fileAccessLog);
	}
	
	@Override
	public List<String> saveAllNewLogs(List<FileAccessLog> fileAccessLogList) {
		return baseDao.saveNewObjects(fileAccessLogList);
	}

	@Override
	@Transactional
	public void deleteObjectsByFileId(String fileId) {
		String sql="delete from file_access_log t where t.file_id=?";
		DatabaseOptUtils.doExecuteSql(baseDao, sql, new Object[]{fileId});
	}

	@Override
	public JSONArray listAccessLog(Map<String, Object> queryParamsMap, PageDesc pageDesc) {
		String queryStatement = 
				"select a.ACCESS_TOKEN, a.FILE_ID, a.AUTH_TIME, a.ACCESS_USERCODE, a.ACCESS_USENAME,"
				+ " a.ACCESS_RIGHT, a.TOKEN_EXPIRE_TIME, a.ACCESS_TIMES, a.LAST_ACCESS_TIME, a.LAST_ACCESS_HOST,"
				+ " b.FILE_NAME  "
				+ " from FILE_ACCESS_LOG a join FILE_STORE_INFO b on (a.FILE_ID=b.FILE_ID) where 1=1 "
				+ " [ :osId | and b.OS_ID = :osId ]"
				+ " [ :(like)fileName | and b.FILE_NAME like :fileName] "
				+ " [ :optId | and b.OPT_ID = :optId ]"
				+ " [ :userCode | and a.ACCESS_USERCODE = :userCode ]"
				+ " [ :beginDate | and a.AUTH_TIME >= :beginDate ]"
				+ " [ :endDate | and a.AUTH_TIME < :endDate ]"
				+ " order by a.AUTH_TIME desc";

		QueryAndNamedParams qap = QueryUtils.translateQuery(queryStatement,queryParamsMap);
		JSONArray dataList = SysDaoOptUtils.listObjectsBySqlAsJson(baseDao, 
				qap.getQuery(), qap.getParams(), 
				null,
				//d 步骤
				/*SysDaoOptUtils
					.createDictionaryMapBuilder("userCode","owenrName", "userCode").create(),*/
				pageDesc);	
		return dataList;
	}
}
