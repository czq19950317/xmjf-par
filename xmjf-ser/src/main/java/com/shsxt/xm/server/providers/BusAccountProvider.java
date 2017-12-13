package com.shsxt.xm.server.providers;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.jdbc.SQL;

public class BusAccountProvider {

    public String getQueryBusAccountByUserIdSql(@Param("userId")Integer userId){
        String sql=new SQL(){
            {
                SELECT("id,user_id as userId,total,usable,cash,frozen,wait,repay");
                FROM("bus_account");
                WHERE("user_id=#{userId}");
            }
        }.toString();
        return sql;
    }
}
