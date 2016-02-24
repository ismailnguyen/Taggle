package com.gnr.esgi.taggle.parser;

import com.gnr.esgi.taggle.model.Article;

public class ArticleParser {
    public static Article parse(Article article) {

        article.setTitle(HtmlParser.parse(article.getTitle()));
        article.setContent(HtmlParser.parse(article.getContent()));

        return article;
    }
}
