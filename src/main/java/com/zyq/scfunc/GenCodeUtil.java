package com.zyq.scfunc;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.StrUtil;

public class GenCodeUtil {

	public static String genSetAndGet(String field, String ObjectType) {
		String getMethod = StrUtil.upperFirstAndAddPre(field,"get");
		String setMethod = StrUtil.upperFirstAndAddPre(field,"set");

		String strGet = "public {} {}() {\n" +
				"    return {};\n" +
				"}";
		strGet =  StrFormatter.format(strGet, ObjectType, getMethod, field);


		String strSet = "public void {}({} {}) {\n" +
				"    this.{} = {};\n" +
				"}";
		strSet =  StrFormatter.format(strSet, setMethod,ObjectType, field,field,field);


		return  strGet+ "\n" + strSet;
	}
}
