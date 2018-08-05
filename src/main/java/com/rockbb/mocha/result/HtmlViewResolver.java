package com.rockbb.mocha.result;

import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class HtmlViewResolver {
	private static Logger logger = LoggerFactory.getLogger(HtmlViewResolver.class);
	private Configuration cfg;
	private String encoding;

	public HtmlViewResolver(String tplPath, String encoding) {
		cfg = new Configuration(Configuration.VERSION_2_3_23);
		cfg.setClassForTemplateLoading(HtmlViewResolver.class, tplPath);
		cfg.setDefaultEncoding(encoding);
		cfg.setLocalizedLookup(false);
		this.encoding = encoding;
	}

	public Configuration getConfiguration() {
		return cfg;
	}

	public void render(HttpServletResponse res, String template, Object data) {
		try {
			res.setContentType("text/html; charset=" + encoding);
			render(res.getWriter(), template, data);
		} catch (IOException e) {
			logger.error("IOException:", e);
		}
	}

	private void render(Writer writer, String template, Object root) {

		try {
			Template t = cfg.getTemplate(template);
			// 使用Environment 代替 Template.process, 是为了设置字符型变量的默认输出格式, 以免产生不必要的错误
			Environment env = t.createProcessingEnvironment(root, writer);
			env.setNumberFormat("#");
			env.process();
		} catch (TemplateException e) {
			logger.error("TemplateException: ", e);
		} catch (IOException e) {
			logger.error("IOException: ", e);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
	}
}