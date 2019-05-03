package com.imooc.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
//保证序列化是null值时响应字段会消失
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable {
    private int status;
    private String msg;
    private T data;
    private ServerResponse(int status){
        this.status =status;
    }
    private ServerResponse(int status,T data){
        this.status = status;
        this.data = data;
    }
    private ServerResponse(int status,String msg){
        this.status=status;
        this.msg = msg;
    }
    private ServerResponse(int status,String msg,T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }
    @JsonIgnore
    public boolean isSucess(){
        return this.status == ResponseCode.SUCESS.getCode();
    }

    public int getStatus(){
        return status;
    }
    public String getMsg(){
        return msg;
    }
    public T getData(){
        return data;
    }

    /**
     *
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCESS.getCode());

    }

    public static <T> ServerResponse<T> createBySucessMessage(String message){
        return new ServerResponse<T>(ResponseCode.SUCESS.getCode(),message);
    }
    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCESS.getCode(),data);
    }
    public static <T> ServerResponse<T> createBySuccess(String msg,T data){
        return new ServerResponse<>(ResponseCode.SUCESS.getCode(),msg,data);
    }

    /**
     *
     * @param <T>
     * @return
     */
    public static <T>ServerResponse<T> createByError(){
        return new ServerResponse<>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }
    public static <T> ServerResponse<T> createByErrorMessage(String message){
        return new ServerResponse<>(ResponseCode.ERROR.getCode(),message);
    }
    /**
     * 说明响应
      */
    public static <T>ServerResponse<T> createByErrorCodeMessage(int errorCode,String errorMessage){
        return new ServerResponse<>(errorCode,errorMessage);
    }
}
