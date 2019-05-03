package com.imooc.service.implement;

import com.google.common.collect.Lists;
import com.imooc.service.interfaces.IFileService;
import com.imooc.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
@Service("iFileService")
public class FileService implements IFileService {
    private Logger logger = LoggerFactory.getLogger(FileService.class);
    @Override
    public String upload(MultipartFile file,String path){
        String fileName = file.getOriginalFilename();
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        logger.info("开始文件上传,上传的文件的文件名为:{},上传的路径为:{},新文件名为:{}",fileName,path,uploadFileName);
        File fileDir = new File(path);
        if(!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path,uploadFileName);
        try{
            file.transferTo(targetFile);
            //文件已经上传成功
//            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
//
//            //将targetFile 上传到ftp服务器
//            targetFile.delete();
            //上传完成后删除upload 文件夹下的相关文件
            logger.info("文件写入成功");
        }catch(IOException err){
            logger.error("文件上传异常",err);
            return null;
        }
        return targetFile.getName();
    }
}
