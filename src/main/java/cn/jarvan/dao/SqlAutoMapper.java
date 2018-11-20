/*
 * 广州丰石科技公司有限公司拥有本软件版权2015并保留所有权利。
 * Copyright 2015, GuangZhou Rich Stone Data Technologies Company Limited, 
 * All rights reserved.
 */
package cn.jarvan.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * <b><code>SqlAutoMapper</code></b>
 * <p>
 * class_comment
 * <p>
 * <b>Creation Time:</b> 2017年12月28日 下午5:23:46
 *
 * @author Zhong Dayang
 * @version 1.0.0-RELEASE 2017年12月28日
 * @since ar-be-cs 1.0.0-RELEASE
 */

public class SqlAutoMapper {
	private final MSUtils msUtils;
	private final SqlSessionFactory sqlSessionFactory;

	/**
     * 构造方法，默认缓存MappedStatement
     *
     * @param sqlSessionFactory
     */
    public SqlAutoMapper(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.msUtils = new MSUtils(sqlSessionFactory.openSession(true).getConfiguration());
    }

	/**
	 * 获取List中最多只有一个的数据
	 *
	 * @param list
	 *            List结果
	 * @param <T>
	 *            泛型类型
	 * @return
	 */
	private <T> T getOne(List<T> list) {
		if (list.size() == 1) {
			return list.get(0);
		} else if (list.size() > 1) {
            throw new TooManyResultsException(
                    "Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
		} else {
			return null;
		}
	}

	/**
	 * 查询返回一个结果，多个结果时抛出异常
	 *
	 * @param sql
	 *            执行的sql
	 * @return
	 */
	public Map<String, Object> selectOne(String sql) {
		List<Map<String, Object>> list = selectList(sql);
		return getOne(list);
	}

	/**
	 * 查询返回一个结果，多个结果时抛出异常
	 *
	 * @param sql
	 *            执行的sql
	 * @param value
	 *            参数
	 * @return
	 */
	public Map<String, Object> selectOne(String sql, Object value) {
		List<Map<String, Object>> list = selectList(sql, value);
		return getOne(list);
	}

	/**
	 * 查询返回一个结果，多个结果时抛出异常
	 *
	 * @param sql
	 *            执行的sql
	 * @param resultType
	 *            返回的结果类型
	 * @param <T>
	 *            泛型类型
	 * @return
	 */
	public <T> T selectOne(String sql, Class<T> resultType) {
		List<T> list = selectList(sql, resultType);
		return getOne(list);
	}

	/**
	 * 查询返回一个结果，多个结果时抛出异常
	 *
	 * @param sql
	 *            执行的sql
	 * @param value
	 *            参数
	 * @param resultType
	 *            返回的结果类型
	 * @param <T>
	 *            泛型类型
	 * @return
	 */
	public <T> T selectOne(String sql, Object value, Class<T> resultType) {
		List<T> list = selectList(sql, value, resultType);
		return getOne(list);
	}

	/**
	 * 查询返回List<Map<String, Object>>
	 *
	 * @param sql
	 *            执行的sql
	 * @return
	 */
	public List<Map<String, Object>> selectList(String sql) {
		String msId = msUtils.select(sql);
		return sqlSessionFactory.openSession().selectList(msId);
	}

	/**
	 * 查询返回List<Map<String, Object>>
	 *
	 * @param sql
	 *            执行的sql
	 * @param value
	 *            参数
	 * @return
	 */
	public List<Map<String, Object>> selectList(String sql, Object value) {
		Class<?> parameterType = value != null ? value.getClass() : null;
		String msId = msUtils.selectDynamic(sql, parameterType);
		return sqlSessionFactory.openSession().selectList(msId, value);
	}

	/**
	 * 查询返回指定的结果类型
	 *
	 * @param sql
	 *            执行的sql
	 * @param resultType
	 *            返回的结果类型
	 * @param <T>
	 *            泛型类型
	 * @return
	 */
	public <T> List<T> selectList(String sql, Class<T> resultType) {
		String msId;
		if (resultType == null) {
			msId = msUtils.select(sql);
		} else {
			msId = msUtils.select(sql, resultType);
		}
		return sqlSessionFactory.openSession().selectList(msId);
	}

	/**
	 * 查询返回指定的结果类型
	 *
	 * @param sql
	 *            执行的sql
	 * @param value
	 *            参数
	 * @param resultType
	 *            返回的结果类型
	 * @param <T>
	 *            泛型类型
	 * @return
	 */
	public <T> List<T> selectList(String sql, Object value, Class<T> resultType) {
		String msId;
		Class<?> parameterType = value != null ? value.getClass() : null;
		if (resultType == null) {
			msId = msUtils.selectDynamic(sql, parameterType);
		} else {
			msId = msUtils.selectDynamic(sql, parameterType, resultType);
		}
		return sqlSessionFactory.openSession().selectList(msId, value);
	}

	/**
	 * 插入数据
	 *
	 * @param sql
	 *            执行的sql
	 * @return
	 */
	public int insert(String sql) {
		String msId = msUtils.insert(sql);
		return sqlSessionFactory.openSession().insert(msId);
	}

	/**
	 * 插入数据
	 *
	 * @param sql
	 *            执行的sql
	 * @param value
	 *            参数
	 * @return
	 */
	public int insert(String sql, Object value) {
		Class<?> parameterType = value != null ? value.getClass() : null;
		String msId = msUtils.insertDynamic(sql, parameterType);
		return sqlSessionFactory.openSession().insert(msId, value);
	}

	/**
	 * 更新数据
	 *
	 * @param sql
	 *            执行的sql
	 * @return
	 */
	public int update(String sql) {
		String msId = msUtils.update(sql);
		return sqlSessionFactory.openSession().update(msId);
	}

	/**
	 * 更新数据
	 *
	 * @param sql
	 *            执行的sql
	 * @param value
	 *            参数
	 * @return
	 */
	public int update(String sql, Object value) {
		Class<?> parameterType = value != null ? value.getClass() : null;
		String msId = msUtils.updateDynamic(sql, parameterType);
		return sqlSessionFactory.openSession().update(msId, value);
	}

	/**
	 * 删除数据
	 *
	 * @param sql
	 *            执行的sql
	 * @return
	 */
	public int delete(String sql) {
		String msId = msUtils.delete(sql);
		return sqlSessionFactory.openSession().delete(msId);
	}

	/**
	 * 删除数据
	 *
	 * @param sql
	 *            执行的sql
	 * @param value
	 *            参数
	 * @return
	 */
	public int delete(String sql, Object value) {
		Class<?> parameterType = value != null ? value.getClass() : null;
		String msId = msUtils.deleteDynamic(sql, parameterType);
		return sqlSessionFactory.openSession().delete(msId, value);
	}

	private class MSUtils {
		private Configuration configuration;
		private LanguageDriver languageDriver;

		@SuppressWarnings("deprecation")
		private MSUtils(Configuration configuration) {
			this.configuration = configuration;
			languageDriver = configuration.getDefaultScriptingLanuageInstance();
		}

		/**
		 * 创建MSID
		 *
		 * @param sql
		 *            执行的sql
		 * @param sql
		 *            执行的sqlCommandType
		 * @return
		 */
		private String newMsId(String sql, SqlCommandType sqlCommandType) {
            StringBuilder msIdBuilder = new StringBuilder(sqlCommandType.toString());
            msIdBuilder.append(".").append(sql.hashCode());
            return msIdBuilder.toString();
		}

		/**
		 * 是否已经存在该ID
		 *
		 * @param msId
		 * @return
		 */
		private boolean hasMappedStatement(String msId) {
			return configuration.hasStatement(msId, false);
		}

		/**
		 * 创建一个查询的MS
		 *
		 * @param msId
		 * @param sqlSource
		 *            执行的sqlSource
		 * @param resultType
		 *            返回的结果类型
		 */
		private void newSelectMappedStatement(String msId, SqlSource sqlSource, final Class<?> resultType) {
			MappedStatement ms = new MappedStatement.Builder(configuration, msId, sqlSource, SqlCommandType.SELECT)
					.resultMaps(new ArrayList<ResultMap>() {
						/**
						 * 序列化id
						 * @since ar-be-cs 1.0.0-RELEASE
						 */
						private static final long serialVersionUID = 1L;

						{
							add(new ResultMap.Builder(configuration, "defaultResultMap", resultType,
                                    new ArrayList<ResultMapping>(0)).build());
						}
					}).build();
			// 缓存
			configuration.addMappedStatement(ms);
		}

		/**
		 * 创建一个简单的MS
		 *
		 * @param msId
		 * @param sqlSource
		 *            执行的sqlSource
		 * @param sqlCommandType
		 *            执行的sqlCommandType
		 */
		private void newUpdateMappedStatement(String msId, SqlSource sqlSource, SqlCommandType sqlCommandType) {
			MappedStatement ms = new MappedStatement.Builder(configuration, msId, sqlSource, sqlCommandType)
					.resultMaps(new ArrayList<ResultMap>() {
						/**
						 * 序列化id
						 * @since ar-be-cs 1.0.0-RELEASE
						 */
						private static final long serialVersionUID = 1L;

						{
							add(new ResultMap.Builder(configuration, "defaultResultMap", int.class,
									new ArrayList<ResultMapping>(0)).build());
						}
					}).build();
			// 缓存
			configuration.addMappedStatement(ms);
		}

		private String select(String sql) {
			String msId = newMsId(sql, SqlCommandType.SELECT);
			if (hasMappedStatement(msId)) {
				return msId;
			}
			StaticSqlSource sqlSource = new StaticSqlSource(configuration, sql);
			newSelectMappedStatement(msId, sqlSource, Map.class);
			return msId;
		}

		private String selectDynamic(String sql, Class<?> parameterType) {
			String msId = newMsId(sql + parameterType, SqlCommandType.SELECT);
			if (hasMappedStatement(msId)) {
				return msId;
			}
			SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, parameterType);
			newSelectMappedStatement(msId, sqlSource, Map.class);
			return msId;
		}

		private String select(String sql, Class<?> resultType) {
			String msId = newMsId(resultType + sql, SqlCommandType.SELECT);
			if (hasMappedStatement(msId)) {
				return msId;
			}
			StaticSqlSource sqlSource = new StaticSqlSource(configuration, sql);
			newSelectMappedStatement(msId, sqlSource, resultType);
			return msId;
		}

		private String selectDynamic(String sql, Class<?> parameterType, Class<?> resultType) {
			String msId = newMsId(resultType + sql + parameterType, SqlCommandType.SELECT);
			if (hasMappedStatement(msId)) {
				return msId;
			}
			SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, parameterType);
			newSelectMappedStatement(msId, sqlSource, resultType);
			return msId;
		}

		private String insert(String sql) {
			String msId = newMsId(sql, SqlCommandType.INSERT);
			if (hasMappedStatement(msId)) {
				return msId;
			}
			StaticSqlSource sqlSource = new StaticSqlSource(configuration, sql);
			newUpdateMappedStatement(msId, sqlSource, SqlCommandType.INSERT);
			return msId;
		}

		private String insertDynamic(String sql, Class<?> parameterType) {
			String msId = newMsId(sql + parameterType, SqlCommandType.INSERT);
			if (hasMappedStatement(msId)) {
				return msId;
			}
			SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, parameterType);
			newUpdateMappedStatement(msId, sqlSource, SqlCommandType.INSERT);
			return msId;
		}

		private String update(String sql) {
			String msId = newMsId(sql, SqlCommandType.UPDATE);
			if (hasMappedStatement(msId)) {
				return msId;
			}
			StaticSqlSource sqlSource = new StaticSqlSource(configuration, sql);
			newUpdateMappedStatement(msId, sqlSource, SqlCommandType.UPDATE);
			return msId;
		}

		private String updateDynamic(String sql, Class<?> parameterType) {
			String msId = newMsId(sql + parameterType, SqlCommandType.UPDATE);
			if (hasMappedStatement(msId)) {
				return msId;
			}
			SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, parameterType);
			newUpdateMappedStatement(msId, sqlSource, SqlCommandType.UPDATE);
			return msId;
		}

		private String delete(String sql) {
			String msId = newMsId(sql, SqlCommandType.DELETE);
			if (hasMappedStatement(msId)) {
				return msId;
			}
			StaticSqlSource sqlSource = new StaticSqlSource(configuration, sql);
			newUpdateMappedStatement(msId, sqlSource, SqlCommandType.DELETE);
			return msId;
		}

		private String deleteDynamic(String sql, Class<?> parameterType) {
			String msId = newMsId(sql + parameterType, SqlCommandType.DELETE);
			if (hasMappedStatement(msId)) {
				return msId;
			}
			SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, parameterType);
			newUpdateMappedStatement(msId, sqlSource, SqlCommandType.DELETE);
			return msId;
		}
	}
}
