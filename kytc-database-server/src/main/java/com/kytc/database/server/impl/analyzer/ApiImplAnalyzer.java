package com.kytc.database.server.impl.analyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.kytc.database.response.ColumnResponse;
import com.kytc.database.server.dto.AnalyzerDTO;
import com.kytc.database.server.dto.ColumnIndexDTO;
import com.kytc.database.server.helper.AnalyzerHelper;
import com.kytc.database.server.service.ayalyzer.Analyzer;
import com.kytc.database.server.utils.DatabaseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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
public class ApiImplAnalyzer implements Analyzer{
    @Autowired
    public ApiImplAnalyzer(AnalyzerHelper analyzerHelper){
        analyzerHelper.putAnalyzer(this);
    }
    @Override
    public List<String> analyzer(AnalyzerDTO analyzerDTO) {
        List<ColumnResponse> columnResponses = analyzerDTO.getColumnResponses();
        String pkg = analyzerDTO.getPkg();
        String tableName = analyzerDTO.getTableName();
        Map<Boolean, Map<String, java.util.List<ColumnIndexDTO>>> columnMap = analyzerDTO.getColumnMap();
        ColumnResponse priColumn = DatabaseUtils.getPriColumn(columnResponses);
        List<String> list = new ArrayList<>();
        list.add("package "+pkg+".server.api.impl;\n");
        list.add("import "+pkg+".request."+ DatabaseUtils.getRequestClass(tableName)+";");
        list.add("import "+pkg+".request."+ DatabaseUtils.getSearchRequestClass(tableName)+";");
        list.add("import "+pkg+".response."+ DatabaseUtils.getResponseClass(tableName)+";");
        list.add("import "+pkg+".api."+ DatabaseUtils.getApiClass(tableName)+";");
        list.add("import "+pkg+".server.service."+ DatabaseUtils.getServiceClass(tableName)+";");
        list.add("import com.kytc.framework.web.common.BasePageResponse;\n");
        list.add("import lombok.RequiredArgsConstructor;");
        list.add("import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.web.bind.annotation.PathVariable;\n" +
                "import org.springframework.web.bind.annotation.RequestBody;\n" +
                "import org.springframework.web.bind.annotation.RequestParam;\n" +
                "import org.springframework.web.bind.annotation.RestController;");
        list.add("\nimport javax.validation.Valid;\n");
        list.add("\n@RestController");
        list.add("@RequiredArgsConstructor(onConstructor_={@Autowired})");
        list.add("public class "+ DatabaseUtils.getApiImplClass(tableName)+" implements "+ DatabaseUtils.getApiClass(tableName)+" {");
        list.add("\tprivate final "+ DatabaseUtils.getServiceClass(tableName)+" "+ DatabaseUtils.getServiceName(tableName)+";");
        list.add("\n\t@Override");
        list.add("\tpublic BasePageResponse<"+ DatabaseUtils.getResponseClass(tableName)+"> listByCondition(\n\t\t" +
                "@RequestBody @Valid "+DatabaseUtils.getSearchRequestClass(tableName) + " request){");
        String line = "\treturn this."+ DatabaseUtils.getServiceName(tableName)+".listByCondition( request );";
        list.add("\t\t"+line);
        list.add("\t}");
        list.add("\n\t@Override");
        list.add("\tpublic Long add(@RequestBody @Valid "+ DatabaseUtils.getRequestClass(tableName)+" request) {");
        list.add("\t\treturn this."+ DatabaseUtils.getServiceName(tableName)+".add(request);");
        list.add("\t}");
        list.add("\n\t@Override");
        list.add("\tpublic boolean update(@RequestBody @Valid "+ DatabaseUtils.getRequestClass(tableName)+" request) {");
        list.add("\t\treturn this."+ DatabaseUtils.getServiceName(tableName)+".update(request);");
        list.add("\t}");
        list.add("\n\t@Override");
        list.add("\tpublic boolean delete(@PathVariable(\"id\") Long id) {");
        list.add("\t\treturn this."+ DatabaseUtils.getServiceName(tableName)+".delete(id);");
        list.add("\t}");
        list.add("\n\t@Override");
        list.add("\tpublic "+ DatabaseUtils.getResponseClass(tableName)+" detail(@PathVariable(\"id\") Long id) {");
        list.add("\t\treturn this."+ DatabaseUtils.getServiceName(tableName)+".detail(id);");
        list.add("\t}");
        if(!CollectionUtils.isEmpty(columnMap) && columnMap.containsKey(false)){
            Map<String,List<ColumnIndexDTO>> map = columnMap.get(false);
            for(String key:map.keySet()){
                List<ColumnIndexDTO> columnIndexDTOList = map.get(key);
                if(columnIndexDTOList.size()==1){
                    if( columnIndexDTOList.get(0).getColumn_name().equalsIgnoreCase(priColumn.getColumnName()) ){
                        continue;
                    }
                }
                list.add("\n\t@Override");
                String line1 = "";
                String column = "";
                for(ColumnIndexDTO columnIndexDTO:columnIndexDTOList){
                    ColumnResponse columnResponse = columnResponses.stream().filter(columnResponse1 -> columnResponse1.getColumnName().equalsIgnoreCase(columnIndexDTO.getColumn_name())).findFirst().get();
                    line1+="@RequestParam(\""+columnResponse.getJavaName()+"\") "+columnResponse.getJavaType()+" "+columnResponse.getJavaName()+",";
                    column+=columnResponse.getJavaName()+",";
                }
                line1 = line1.substring(0,line1.length()-1);
                column = column.substring(0,column.length()-1);
                list.add("\tpublic boolean delete("+line1+"){");
                list.add("\t\treturn this."+DatabaseUtils.getServiceName(tableName)+".delete("+column+");\n\t}");
            }
        }
        list.add("}");
        return list;
    }

    @Override
    public String getFileName(String tableName) {
        return DatabaseUtils.getApiImplClass(tableName)+".java";
    }

    @Override
    public String getPackage() {
        return "api"+ File.separator+"impl";
    }
}
