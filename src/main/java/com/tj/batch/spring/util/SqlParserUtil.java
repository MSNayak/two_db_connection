package com.tj.batch.spring.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;

import org.apache.log4j.Logger;

/**
 * 
 * @author Mithun
 * @date 05 May 2015.
 */
public class SqlParserUtil {
	private static final Logger logger = Logger.getLogger(SqlParserUtil.class);

	public static List<String> getColumnsForUpdate(String sqltext)
			throws JSQLParserException {
		List<String> columns = new ArrayList<String>();

		sqltext = replaceDBSpecificColumns(sqltext);
		try {
			Update update = (Update) CCJSqlParserUtil.parse(new StringReader(
					sqltext));
			List<Column> clms = update.getColumns();
			List<Expression> clExps = update.getExpressions();
			String[] whereColumns = update.getWhere().toString().split("AND");
			int i = 0;
			for (Column cl : clms) {
				if (clExps.get(i) instanceof JdbcParameter) {
					columns.add(getReplacedColumn(cl.getColumnName()));
				}

				i++;
			}
			for (String wc : whereColumns) {
				String[] columnValue = wc.trim().split("=");
				if (columnValue[1].trim().equals("?")) {
					columns.add(getReplacedColumn(columnValue[0]));
				}
			}
		} catch (JSQLParserException e) {
			// e.printStackTrace();
			logger.error(e + " query not parse:-" + sqltext + " "
					+ e.getCause());
			throw new JSQLParserException();
		}
		return columns;
	}

	public static List<String> getColumnsForInsert(String sqlInsert) {
		List<String> columns = new ArrayList<String>();
		sqlInsert = replaceDBSpecificColumns(sqlInsert);
		;
		try {
			Insert insert = (Insert) CCJSqlParserUtil.parse(new StringReader(
					sqlInsert));
			List<Column> clms = insert.getColumns();
			for (Column cl : clms) {
				columns.add(getReplacedColumn(cl.getColumnName()));
			}
		} catch (JSQLParserException e) {
			// e.printStackTrace();
			logger.error(e + " query not parse:-" + sqlInsert + " "
					+ e.getCause());
			throw new RuntimeException(e.getCause());
		}
		return columns;
	}

	public static List<String> getWhereColumnsForSelect(String sqlselect) {
		List<String> columns = new ArrayList<String>();
		sqlselect = sqlselect.toUpperCase().replace("WITH UR", "");
		try {
			Select select = (Select) CCJSqlParserUtil.parse(new StringReader(
					sqlselect));
			PlainSelect ps = (PlainSelect) select.getSelectBody();
			String[] whereColumns = ps.getWhere().toString().split("AND");
			for (String wc : whereColumns) {
				columns.add(wc.trim().split("=")[0].trim());
			}
		} catch (JSQLParserException e) {
			logger.error(e + " query not parse:-" + sqlselect + " "
					+ e.getCause());
			throw new RuntimeException(e.getCause());
		}
		return columns;
	}

	private static String replaceDBSpecificColumns(String sqlText) {
		String from = "K_FROM";
		String view = "K_VIEW";
		sqlText = sqlText.toUpperCase().replaceAll("FROM", from)
				.replaceAll("VIEW", view);
		return sqlText;
	}

	private static String getReplacedColumn(String column) {
		String from = "K_FROM";
		String view = "K_VIEW";
		column = column.trim().replaceAll(from, "FROM")
				.replaceAll(view, "VIEW").trim();
		return column;
	}
}
