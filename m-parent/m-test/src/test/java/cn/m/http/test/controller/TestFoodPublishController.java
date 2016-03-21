package cn.m.http.test.controller;

import java.util.HashMap;
import java.util.Map;

import cn.m.http.test.base.BaseRequest;

public class TestFoodPublishController {

	@SuppressWarnings("serial")
	static final Map<String, Map<String, String>> arrary = new HashMap<String, Map<String, String>>() {
		{

			put("food/publishFoodData.do", new HashMap<String, String>() {
				{

					String publishData = "{    \"foodPoi\": {        \"category\": \"\",        \"address\": \"北京市朝阳区芳园南街芳园里9号楼楼下\","
							+ "        \"longitude\": 116.485481262207,        \"adCode\": \"\",        \"isSave\": false,"
							+ "        \"title\": \"黑子东北菜馆\",        \"id\": \"5234183861936145916\",        \"latitude\": 39.97357177734375,"
							+ "        \"tel\": \"\",        \"type\": 0    },    \"bFav\": false,    \"headImage\": \"123\","
							+ "    \"id\": \"4a2c781a30c76466321f4fc566e7de9e\",    \"numberOfPeople\": 25,    \"frontCoverContent\": {"
							+ "        \"tagImage\": {            \"points\": [],            \"id\": \"6b97b6feb6e6e63d08ec1c4b9991f51c\",            \"imageToken\": \"\","
							+ "            \"imageComment\": 0        },        \"id\": \"a13f2998862951d1c70b3618d06e9f99\","
							+ "        \"content\": \"你大爷\"    },    \"totalPrice\": 333,    \"richTexts\": [        {"
							+ "            \"tagImage\": {                \"points\": [],                \"id\": \"6483837c938e7100481e0ebc20a2af78\","
							+ "                \"imageToken\": \"4a2c781a30c76466321f4fc566e7de9eimg3\",                \"imageComment\": 0"
							+ "            },            \"id\": \"3379ab8f813f473a0069ba678c6d2401\",            \"content\": \"哈哈哈\""
							+ "        },        {            \"tagImage\": {                \"points\": [],                \"id\": \"74e8ad8d47c31dce94e13f09e7121868\","
							+ "                \"imageToken\": \"4a2c781a30c76466321f4fc566e7de9eimg2\",                \"imageComment\": 0"
							+ "            },            \"id\": \"8e7fa0ccb4dac96bca87f66553cf2e21\",            \"content\": \"弩哥\""
							+ ""
							+ "        },"
							+ "        {            \"tagImage\": {                \"points\": [],                \"id\": \"090788b17c803e7c1e67f6b4bcebc2e7\","
							+ "                \"imageToken\": \"4a2c781a30c76466321f4fc566e7de9eimg5\",                \"imageComment\": 0            },"
							+ "            \"id\": \"e951a6dbd008b6fe9b73e3a29b7d4344\",            \"content\": \"小罗啦啦啦上帝亲吻大地于是罗纳尔迪尼奥就诞生了哈哈哈\"        }"
							+ "    ],    \"foodType\": 4}";
					put("publishData", publishData);
					put("cusId", "4a2c781a30c76466321f4fc566e7de9e");
					put("headImage", "asd");
					put("account", "4p21ohewh7b80cil");
				}
			});

		}
	};

	public static void main(String[] args) {
		BaseRequest.getResult(arrary);
	}
}
