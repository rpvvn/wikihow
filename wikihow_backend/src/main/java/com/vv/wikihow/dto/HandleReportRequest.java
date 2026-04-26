package com.vv.wikihow.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 处理举报请求 DTO
 * 
 * 用于审核员/管理员处理用户举报的过时内容。
 * 支持两种处理方式：
 * 1. 标记文章为过时 - 保留文章但标记为过时状态
 * 2. 删除文章 - 直接删除违规文章
 * 
 * @author WikiHow Team
 */
@Data
public class HandleReportRequest {
    
    /**
     * 处理结果
     * 
     * 取值说明：
     * - 1: 已处理（根据handleType执行相应操作）
     * - 2: 已忽略（驳回举报，不做任何处理）
     * 
     * 必填项
     */
    @NotNull(message = "处理结果不能为空")
    private Integer status;
    
    /**
     * 处理类型
     * 
     * 仅当 status=1（已处理）且为过时举报时有效，指定具体的处理方式：
     * - 1: 标记过时 - 将文章标记为过时状态，用户仍可查看但会显示警告
     * - 2: 删除文章 - 直接删除文章，用户无法访问
     * 
     * 注意：复核申请不需要此字段
     */
    private Integer handleType;
    
    /**
     * 处理备注
     * 
     * 审核员对此举报的处理说明，用户可在通知中看到。
     * 例如："内容已过时，已标记为过时"、"文章违反社区规则，已删除"
     * 
     * 可选项，最多500字
     */
    @Size(max = 500, message = "处理备注不能超过500字")
    private String comment;
    
    /**
     * 处理原因
     * 
     * 当 status=1（已处理）时，根据handleType的含义：
     * - handleType=1时：过时原因（为什么标记为过时）
     * - handleType=2时：删除原因（为什么删除文章）
     * 
     * 可选项，最多500字
     */
    @Size(max = 500, message = "原因不能超过500字")
    private String reason;
}
