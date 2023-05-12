package com.example.community.dto;

import lombok.Data;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationDTO {
    private List<QuestionDTO> questionDTOList;

    private boolean showPreviousButton;
    private boolean showFirstButton;
    private boolean showNextButton;
    private boolean showEndButton;

    private Integer currentPage;
    private List<Integer> surroundingPages = new ArrayList<>();  // 用于展示这一页周围的页面
    private Integer totalPage;
    public void setPage(Integer totalPage, Integer page, Integer size) {
        Logger logger = Logger.getLogger(this.getClass());
        logger.info("每页" + size + "条，总页数为：" + totalPage);
        logger.info("当前页面：" + page);

        this.currentPage = page;
        this.totalPage = totalPage;
        this.surroundingPages.add(page);
        for (int i=1; i<=3; i++) {
            if (page - i > 0)
                this.surroundingPages.add(0, page-i);
            if (page + i <= totalPage)
                this.surroundingPages.add(page+i);
        }

        // 是否展示上一页按钮
        if (page == 1) showPreviousButton = false;
        else showPreviousButton = true;
        // 是否展示下一页按钮
        if (page.equals(totalPage)) showNextButton = false;
        else showNextButton = true;
        // 是否展示首页按钮
        if (surroundingPages.contains(1))
            showFirstButton = false;
        else
            showFirstButton = true;
        // 是否展示尾页按钮
        if (surroundingPages.contains(totalPage))
            showEndButton = false;
        else
            showEndButton = true;
    }
}
