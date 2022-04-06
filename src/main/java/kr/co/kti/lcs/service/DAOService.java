package kr.co.kti.lcs.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;
import kr.co.kti.lcs.jdbc.MySQLConnectionPool;
import kr.co.kti.lcs.vo.LoopDtgINFOBody;

/**
 * DAOService Query 
 * 
 * @author yeski
 *
 */
public class DAOService
{
	/**
	 * Define Log
	 */
	private org.slf4j.Logger log = null;

	/**
	 * Define INFO Body(VO)
	 */
	private LoopDtgINFOBody loopDtgINFOBody;
	
	/**
	 * Define DB Pool
	 */
	private MySQLConnectionPool pool;

	/**
	 * Instance of DAOService 
	 * @param pool
	 */
	public DAOService(MySQLConnectionPool pool)
	{
		log = LoggerFactory.getLogger("process.DAOManagerWorker");
		this.pool = pool;
	}

	/**
	 * 회선번호 기준 Trip Info 불러오기 
	 * 
	 * @param strIdentityNo
	 * @return
	 */
	public JsonObject getTripInfo(String strIdentityNo)
	{
		/**
		 * Local Connection
		 */
		Connection conn = null;
		
		/**
		 * 데이터 Structure JSON
		 */
		JsonObject joReturn = new JsonObject();

		try
		{
			StringBuilder sbSql = new StringBuilder();

			sbSql.append("SELECT DVC_ID");
			sbSql.append("	   , DRIVER_CODE");
			sbSql.append("     , MODEL_NO");
			sbSql.append("     , CAR_ID_NO");
			sbSql.append("     , CAR_REG_NO");
			sbSql.append("     , BIZ_NO");
			sbSql.append("     , CAR_TYPE");
			sbSql.append("     , TRIP_SEQ");
			sbSql.append("     , DRIVING_INFO_CODE");
			sbSql.append("     , TRIP_COUNT");
			sbSql.append("  FROM LOOP_INFO");
			sbSql.append(" WHERE DVC_ID = '%s'");

			String strSql = sbSql.toString();

			strSql = String.format(strSql, strIdentityNo);

			conn = this.pool.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(strSql);

			while (rs.next())
			{
				joReturn.put("DVC_ID", rs.getString("DVC_ID"));
				joReturn.put("DRIVER_CODE", rs.getString("DRIVER_CODE"));
				joReturn.put("MODEL_NO", rs.getString("MODEL_NO"));
				joReturn.put("CAR_ID_NO", rs.getString("CAR_ID_NO"));
				joReturn.put("CAR_REG_NO", rs.getString("CAR_REG_NO"));
				joReturn.put("BIZ_NO", rs.getString("BIZ_NO"));
				joReturn.put("CAR_TYPE", rs.getInt("CAR_TYPE"));
				joReturn.put("TRIP_SEQ", rs.getInt("TRIP_SEQ"));
				joReturn.put("DRIVING_INFO_CODE", rs.getString("DRIVING_INFO_CODE"));
				joReturn.put("TRIP_COUNT", rs.getInt("TRIP_COUNT"));
			}

		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (conn != null)
				{
					log.debug("Connection 반환");
					this.pool.returnConnection(conn);
				}
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return joReturn;
	}

	/**
	 * 회선번호 기준 청약 상태 확인
	 * 
	 * @param strIdentityNo
	 * @return
	 */
	public int authIdentityNo(String strIdentityNo)
	{
		/**
		 * Local Connection
		 */
		Connection conn = null;
		
		/**
		 * Return Count
		 */
		int intReturn = 0;

		log.debug(strIdentityNo);

		String strSql = "SELECT COUNT(DVC_ID) CNT FROM SBSC WHERE DVC_ID = '%s'";
		strSql = String.format(strSql, strIdentityNo);

		log.debug("strSql:" + strSql);

		try
		{
			conn = this.pool.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(strSql);

			while (rs.next())
			{
				intReturn = rs.getInt("CNT");
			}
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (conn != null)
				{
					log.debug("Connection 반환");
					this.pool.returnConnection(conn);
				}
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return intReturn;
	}

	/**
	 * 회선번호 기준 Info Data 설정 
	 * 
	 * @param strIdentityNo
	 * @param loopDtgINFOBody
	 */
	public void setLoopDtgInfo(String strIdentityNo, LoopDtgINFOBody loopDtgINFOBody)
	{
		/**
		 * Local Connection
		 */
		Connection conn = null;
		
		// DB에 설정 한다
		this.loopDtgINFOBody = loopDtgINFOBody;

		StringBuilder sbSql = new StringBuilder();

		sbSql.append("INSERT INTO LOOP_INFO (DVC_ID, DRIVER_NAME, DRIVER_CODE, CAR_TYPE, CAR_REG_NO, CAR_ID_NO, OFFICE_NAME, BIZ_NO, TYPE_APPROVAL_NO, SERIAL_NO, MODEL_NO, K_FACTOR, RPM_FACTOR, TRIP_SEQ, DRIVING_INFO_CODE, TRIP_COUNT)");
		sbSql.append(" VALUES ('%s', '%s', '%s', %d, '%s', '%s', '%s', '%s', '%s', '%s', '%s', %d, %d, 1495193626, 'S', 1)");
		sbSql.append(" ON DUPLICATE KEY UPDATE");
		sbSql.append("    DRIVER_NAME = '%s'");
		sbSql.append("	, DRIVER_CODE = '%s'");
		sbSql.append("	, CAR_TYPE = %d");
		sbSql.append("	, CAR_REG_NO = '%s'");
		sbSql.append("	, CAR_ID_NO = '%s'");
		sbSql.append("	, OFFICE_NAME = '%s'");
		sbSql.append("	, BIZ_NO = '%s'");
		sbSql.append("	, TYPE_APPROVAL_NO = '%s'");
		sbSql.append("	, SERIAL_NO = '%s'");
		sbSql.append("	, MODEL_NO = '%s'");
		sbSql.append("	, K_FACTOR = %d");
		sbSql.append("	, RPM_FACTOR = %d");

		String strSql = String.format(sbSql.toString(), "0" + this.loopDtgINFOBody.getIdentityNo(), this.loopDtgINFOBody.getDriverName(), this.loopDtgINFOBody.getDriverCode(), loopDtgINFOBody.getCarType(), this.loopDtgINFOBody.getCarRegNo(), this.loopDtgINFOBody.getCarIdNo(),
				this.loopDtgINFOBody.getOfficeName(), this.loopDtgINFOBody.getBizNo(), this.loopDtgINFOBody.getTypeApprovalNo(), this.loopDtgINFOBody.getSerialNo(), this.loopDtgINFOBody.getModelNo(), this.loopDtgINFOBody.getKFactor(), this.loopDtgINFOBody.getRPMFactor(),
				this.loopDtgINFOBody.getDriverName().trim(), this.loopDtgINFOBody.getDriverCode(), loopDtgINFOBody.getCarType(), this.loopDtgINFOBody.getCarRegNo(), this.loopDtgINFOBody.getCarIdNo(), this.loopDtgINFOBody.getOfficeName(), this.loopDtgINFOBody.getBizNo(),
				this.loopDtgINFOBody.getTypeApprovalNo(), this.loopDtgINFOBody.getSerialNo(), this.loopDtgINFOBody.getModelNo(), this.loopDtgINFOBody.getKFactor(), this.loopDtgINFOBody.getRPMFactor());

		log.debug(strSql);

		try
		{
			conn = this.pool.getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(strSql);
			conn.commit();
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("DB Error : " + e.getMessage());
			
			try
			{
				conn.rollback();
			}
			catch (SQLException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		finally
		{
			try
			{
				if (conn != null)
				{
					log.debug("Connection 반환");
					this.pool.returnConnection(conn);
				}
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 회선번호 기준 트립시퀀스 및 운행코드 설정 
	 * 
	 * @param strIdentityNo
	 * @param joTripInfo
	 */
	public void setTripInfo(String strIdentityNo, JsonObject joTripInfo)
	{
		/**
		 * Local Connection
		 */
		Connection conn = null;
		
		StringBuilder sbSql = new StringBuilder();

		sbSql.append("UPDATE LOOP_INFO");
		sbSql.append("   SET TRIP_COUNT = %d ");
		sbSql.append("	   , TRIP_SEQ = %d");
		sbSql.append("	   , DRIVING_INFO_CODE = '%s'");
		sbSql.append(" WHERE DVC_ID = '%s'");

		if (joTripInfo != null)
		{
			String strSql = String.format(sbSql.toString(), joTripInfo.getInteger("TRIP_COUNT"), joTripInfo.getInteger("TRIP_SEQ"), joTripInfo.getString("DRIVING_INFO_CODE"), strIdentityNo);

			try
			{
				conn = this.pool.getConnection();
				Statement stmt = conn.createStatement();
				stmt.executeUpdate(strSql);
				conn.commit();
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				try
				{
					conn.rollback();
				}
				catch (SQLException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			finally
			{
				try
				{
					if (conn != null)
					{
						this.pool.returnConnection(conn);
					}
				}
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 회선번호 기준 청약이 없을경우 INFO Data 삭제
	 * 
	 * @param strIdentityNo
	 */
	public void deleteLoopInfo(String strIdentityNo)
	{
		/**
		 * Local Connection
		 */
		Connection conn = null;
		
		String strSql = "DELETE FROM LOOP_INFO WHERE DVC_ID = '%s'";

		strSql = String.format(strSql, strIdentityNo);

		try
		{
			conn = this.pool.getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(strSql);
			conn.commit();
			
			log.debug("ConnCount:" + this.pool.getConnCount());
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			try
			{
				conn.rollback();
			}
			catch (SQLException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		finally
		{
			try
			{
				if (conn != null)
				{
					log.debug("Connection 반환");
					this.pool.returnConnection(conn);
					
					log.debug("ConnCount:" + this.pool.getConnCount());
				}
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
