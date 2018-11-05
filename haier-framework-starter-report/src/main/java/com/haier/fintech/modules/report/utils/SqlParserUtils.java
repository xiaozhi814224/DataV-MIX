package com.haier.fintech.modules.report.utils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
//SQL语句解析获取列名
public class SqlParserUtils {
	public static List<String> parseColumnName(String sql) {
		CCJSqlParserManager parserManager = new CCJSqlParserManager();
		Select select = null;
		try {
			select = (Select) parserManager.parse(new StringReader(sql));
		} catch (JSQLParserException e) {
			e.printStackTrace();
		}
		PlainSelect plain = (PlainSelect) select.getSelectBody();
		List<SelectItem> selectitems = plain.getSelectItems();
		List<String> str_items = new ArrayList<String>();
		if (selectitems != null) {
			for (int i = 0; i < selectitems.size(); i++) {
				String[] str = selectitems.get(i).toString().split(" ");
				if(str.length == 3) {//带有别名					
					String columnName = str[2];
					str_items.add(columnName.replaceAll("\"",""));
				}else {
					str_items.add(selectitems.get(i).toString().replaceAll("\"",""));
				}
			}
		}
		return str_items;
	}

}
