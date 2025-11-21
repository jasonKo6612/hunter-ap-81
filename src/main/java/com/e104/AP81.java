package com.e104;

/**
 * History： <br>
 * 2008/8/13 Sean.Chen Created<br>
 * 2008/11/12 Sean.chen modify BEP00-00001-1237 獵才派遣共用元件整合專案<br> 
 * 2012/09/06 sally.huang modify db migration<br>
 * 2012/10/08 update by Josie Wu at BEP00-00001-1462 MySQL轉換作業專案<br>
 * 2015/10/30 update by Josie Wu at BEP00-00001-1538 機敏性欄位加密處理(AES)(1)<br>
 * 2016/09/05 update by Peter.Tsai at 調整AP架構<br>
 * 20230117 Peter Tsai HTHUNTERREQ-1237 [獵才]AP 版更
 */

//import java.io.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ht.util.XmlGlobalHandlerNewAP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import com.ht.util.XmlLocalHandlerNewAP;

/**
 * Description： 104人選聯絡紀錄 <br>
 * Classname：AP81.java<br>
 * Date：2008/8/13<br>
 * Author：Sean Chen<br>
 * Copyright (c) 104hunter All Rights Reserved.<br>
 */
public class AP81 {
    private static final Logger logger = LoggerFactory.getLogger(AP81.class);

    private static XmlGlobalHandlerNewAP globalXML = null;
    //private static XmlLocalHandlerNewAP localXML = null;

    // Log
//    private static File fLogFile = null;
//    private static BufferedWriter bwLogFile = null;
//    private static PrintWriter pw = null;
//    private static SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy/MM/dd hh:mm:ss" );

    /**
     * <summary>查詢資料</summary><br>
     * @param
     * @return
     * @throws IOException
     */
    private void queryData() throws IOException {
        Connection conHun = null;
        PreparedStatement pstHun = null;
        PreparedStatement pstHun2 = null;
        PreparedStatement pstHun3 = null;
        ResultSet rsHun = null;
        ResultSet rsHun2 = null;
        try {
            // 建立Connection
            Class.forName( globalXML.getGlobalTagValue( "dsn1.driver" ) );
            conHun = DriverManager.getConnection( globalXML.getGlobalTagValue( "dsn1.database" ), globalXML.getGlobalTagValue( "dsn1.username" ), globalXML.getGlobalTagValue( "dsn1.password" ) );
            pstHun = conHun.prepareStatement( "select contact_id,rid from contact_rec where rid<>'0' and (contact_name='' or contact_name is null) order by Rid" );
            pstHun2 = conHun.prepareStatement( "Select CName,EName From Resume where Rid=?" );
            pstHun3 = conHun.prepareStatement( "Update Contact_Rec set contact_name=? where contact_id=?" );
            logger.info("##現有連絡紀錄中 Rid<>0 之人選編號)SQL：select contact_id,rid from contact_rec where rid<>0 and (contact_name='' or contact_name is null) order by Rid");

            rsHun = pstHun.executeQuery();
            while( rsHun.next() ) {
                logger.info("##                               人選編號：{}", rsHun.getString( "rid" ));
                pstHun2.clearParameters();
                pstHun2.setString( 1, rsHun.getString( "rid" ) );
                rsHun2 = pstHun2.executeQuery();
                while( rsHun2.next() ) {
                    String strName = "";
                    if( rsHun2.getString( "CName" ) != null && !rsHun2.getString( "CName" ).equals( "" ) ) {
                        strName = rsHun2.getString( "CName" );
                    } else if( rsHun2.getString( "EName" ) != null && !rsHun2.getString( "EName" ).equals( "" ) ) {
                        strName = rsHun2.getString( "EName" );
                    }
                    //logger.debug("##                                人選姓名：{}", strName);
                    // 更新姓名
                    if( strName.length() != 0 ) {
                        pstHun3.clearParameters();
                        pstHun3.setString( 1, strName );
                        pstHun3.setString( 2, rsHun.getString( "contact_id" ) );
                        pstHun3.executeUpdate();
                    }
                }
            }
        } catch( ClassNotFoundException e ) {
            logger.error("queryData() ClassNotFoundException:", e);
        } catch( SQLException e ) {
            logger.error("queryData() SQLException:", e);
        } finally {
            if( rsHun2 != null ) {
                try {
                    rsHun2.close();
                } catch( SQLException e ) {
                    logger.warn("queryData() close rsHun2 exception:", e);
                }
            }
            if( rsHun != null ) {
                try {
                    rsHun.close();
                } catch( SQLException e ) {
                    logger.warn("queryData() close rsHun exception:", e);
                }
            }
            if( pstHun3 != null ) {
                try {
                    pstHun3.close();
                } catch( SQLException e ) {
                    logger.warn("queryData() close pstHun3 exception:", e);
                }
            }
            if( pstHun2 != null ) {
                try {
                    pstHun2.close();
                } catch( SQLException e ) {
                    logger.warn("queryData() close pstHun2 exception:", e);
                }
            }
            if( pstHun != null ) {
                try {
                    pstHun.close();
                } catch( SQLException e ) {
                    logger.warn("queryData() close pstHun exception:", e);
                }
            }
            if( conHun != null ) {
                try {
                    conHun.close();
                } catch( SQLException e ) {
                    logger.warn("queryData() close connection exception:", e);
                }
            }
        }
    }


    public static void main( String[] args ) throws IOException {
        try {
            // 設定xml
            //localXML = XmlLocalHandlerNewAP.performParser();
            globalXML = XmlGlobalHandlerNewAP.performParser( 81, "" );
            // 設定file
//            fLogFile = new File( globalXML.getGlobalTagValue( "apini.logpath" ) + "AP81_" + new SimpleDateFormat( "yyyyMMdd" ).format( new Date() ) + ".log" );
//            fLogFile.createNewFile();
//            bwLogFile = new BufferedWriter( new FileWriter( fLogFile.getPath(), true ) );
//            pw = new PrintWriter( bwLogFile );
            logger.info("========== START ==========");

            AP81 newJ81 = new AP81();
            newJ81.queryData();

            logger.info("==========  END ==========");
        } catch( Exception e ) {
            logger.error("main exception:", e);
        } finally {
            //localXML = null;
            globalXML = null;
        }
    }
}
