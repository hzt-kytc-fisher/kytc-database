package com.kytc.database.server.impl.analyzer;

import java.util.ArrayList;
import java.util.List;

import com.kytc.database.server.dto.AnalyzerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kytc.database.response.ColumnResponse;
import com.kytc.database.server.helper.AnalyzerHelper;
import com.kytc.database.server.service.ayalyzer.Analyzer;
import com.kytc.database.server.utils.DatabaseUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * <a style="display:none">简单描述</a>.
 * <a style="display:none">详细描述</a><p></p>
 * <p><strong>目的:</strong></p>
 * <p><strong>原因:</strong></p>
 * <p><strong>用途:</strong></p>
 *
 * @author: 何志同
 * @date: 2019-08-24 21:57
 * @see <a target="_blank" href="">参考文档</a>
 **/
@Component
@Slf4j
public class RequestAnalyzer implements Analyzer{
    @Autowired
    public RequestAnalyzer(AnalyzerHelper analyzerHelper){
        analyzerHelper.putAnalyzer(this);
    }
    @Override
    public List<String> analyzer(AnalyzerDTO analyzerDTO) {
        List<ColumnResponse> columnResponses = analyzerDTO.getColumnResponses();
        String pkg = analyzerDTO.getPkg();
        String tableName = analyzerDTO.getTableName();
        String description = analyzerDTO.getDescription();
        List<String> list = new ArrayList<>();
        list.add("package "+pkg+".request;\n");
        list.add("import lombok.Data;");
        list.add("import io.swagger.annotations.ApiModel;");
        list.add("import io.swagger.annotations.ApiModelProperty;");
        list.add("\nimport javax.validation.constraints.NotNull;");
        list.add("import java.io.Serializable;");
        list.add("import java.util.Date;");
        list.add("\n@Data");
        list.add("@ApiModel(\""+description+" request\")");
        list.add("public class "+ DatabaseUtils.getRequestClass(tableName)+" implements Serializable {");
        list.add("\tprivate static final long serialVersionUID = 1L;");
        for(ColumnResponse columnResponse:columnResponses){
            if("DEFAULT_GENERATED on update CURRENT_TIMESTAMP(6)".equalsIgnoreCase(columnResponse.getExtra())){
                continue;
            }
            if("NO".equals(columnResponse.getNullable())&&!DatabaseUtils.isAutoIncrement(columnResponse)){
                list.add("\t@NotNull( message = \""+columnResponse.getColumnComment()+"不能为空\" )");
            }
            list.add("\t@ApiModelProperty(\""+columnResponse.getColumnComment()+"\")");
            list.add("\tprivate "+ columnResponse.getJavaType()+" "+ columnResponse.getJavaName()+";");
        }
        list.add("}");
        return list;
    }

    @Override
    public String getFileName(String tableName) {
        return DatabaseUtils.getRequestClass(tableName)+".java";
    }

    @Override
    public String getPackage() {
        return "request";
    }
}
