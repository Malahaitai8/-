package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Complaint;
import com.example.springboot.exception.CustomException;
import com.example.springboot.service.ComplaintService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/complaint")
public class ComplaintController {

    @Resource
    private ComplaintService complaintService;



    /**
     * 提交新的投诉
     * API: POST /complaint/submit
     */
    @PostMapping("/submit")
    public Result submitComplaint(@RequestBody Complaint complaint) {
        try {
            complaintService.submitComplaint(complaint);
            return Result.success("投诉提交成功");
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            // Log the exception e
            return Result.error("500", "投诉提交失败: " + e.getMessage());
        }
    }

    /**
     * 更新投诉详情 (通常由管理员操作)
     * API: PUT /complaint/updateDetails
     */
    @PutMapping("/updateDetails")
    public Result updateComplaintDetails(@RequestBody Complaint complaint) {
        try {
            complaintService.updateComplaintDetails(complaint);
            return Result.success("投诉详情更新成功");
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            return Result.error("500", "更新投诉详情失败: " + e.getMessage());
        }
    }

    /**
     * 管理员处理投诉
     * API: PUT /complaint/process
     * Request Body: { "complaintId": "C001", "processingStatus": "处理中", "processingResult": "已联系双方沟通", "handlerAdminId": "ADM001" }
     */
    @PutMapping("/process")
    public Result processComplaint(@RequestBody Map<String, String> payload) {
        try {
            String complaintId = payload.get("complaintId");
            String processingStatus = payload.get("processingStatus");
            String processingResult = payload.get("processingResult");
            String handlerAdminId = payload.get("handlerAdminId");

            complaintService.processComplaint(complaintId, processingStatus, processingResult, handlerAdminId);
            return Result.success("投诉处理信息更新成功");
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            return Result.error("500", "处理投诉失败: " + e.getMessage());
        }
    }


    /**
     * 根据投诉ID删除投诉
     * API: DELETE /complaint/delete/{complaintId}
     */
    @DeleteMapping("/delete/{complaintId}")
    public Result deleteComplaint(@PathVariable String complaintId) {
        try {
            complaintService.deleteComplaintById(complaintId);
            return Result.success("投诉删除成功");
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            return Result.error("500", "删除投诉失败: " + e.getMessage());
        }
    }

    /**
     * 根据投诉ID查询投诉详情
     * API: GET /complaint/get/{complaintId}
     */
    @GetMapping("/get/{complaintId}")
    public Result getComplaintById(@PathVariable String complaintId) {
        try {
            Complaint complaint = complaintService.getComplaintById(complaintId);
            if (complaint != null) {
                return Result.success(complaint);
            } else {
                return Result.error("404", "未找到指定的投诉");
            }
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            return Result.error("500", "查询投诉详情失败: " + e.getMessage());
        }
    }

    /**
     * 查询所有投诉 (可带过滤条件)
     * API: GET /complaint/all
     * Query Params from Complaint entity for filtering
     */
    @GetMapping("/all")
    public Result getAllComplaints(Complaint complaint) {
        try {
            List<Complaint> complaints = complaintService.getAllComplaints(complaint);
            return Result.success(complaints);
        } catch (Exception e) {
            return Result.error("500", "查询所有投诉失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询投诉
     * API: GET /complaint/page
     * Query Params: pageNum, pageSize, and fields from Complaint for filtering
     */
    @GetMapping("/page")
    public Result getComplaintPage(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   Complaint complaint) {
        try {
            PageInfo<Complaint> pageInfo = complaintService.getComplaintPage(complaint, pageNum, pageSize);
            return Result.success(pageInfo);
        } catch (Exception e) {
            return Result.error("500", "分页查询投诉失败: " + e.getMessage());
        }
    }

    /**
     * 根据发起人ID查询投诉
     * API: GET /complaint/byComplainant/{complainantId}
     */
    @GetMapping("/byComplainant/{complainantId}")
    public Result getComplaintsByComplainantId(@PathVariable String complainantId) {
        try {
            List<Complaint> complaints = complaintService.getComplaintsByComplainantId(complainantId);
            return Result.success(complaints);
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            return Result.error("500", "按发起人查询投诉失败: " + e.getMessage());
        }
    }

    /**
     * 根据投诉对象ID查询投诉
     * API: GET /complaint/byTarget/{complaintTargetId}
     */
    @GetMapping("/byTarget/{complaintTargetId}")
    public Result getComplaintsByComplaintTargetId(@PathVariable String complaintTargetId) {
        try {
            List<Complaint> complaints = complaintService.getComplaintsByComplaintTargetId(complaintTargetId);
            return Result.success(complaints);
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            return Result.error("500", "按投诉对象查询投诉失败: " + e.getMessage());
        }
    }

    /**
     * 根据处理状态查询投诉
     * API: GET /complaint/byStatus
     * Query Param: status
     */
    @GetMapping("/byStatus")
    public Result getComplaintsByProcessingStatus(@RequestParam String status) {
        try {
            List<Complaint> complaints = complaintService.getComplaintsByProcessingStatus(status);
            return Result.success(complaints);
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            return Result.error("500", "按处理状态查询投诉失败: " + e.getMessage());
        }
    }

    /**
     * 根据投诉类型查询投诉
     * API: GET /complaint/byType
     * Query Param: type
     */
    @GetMapping("/byType")
    public Result getComplaintsByComplaintType(@RequestParam String type) {
        try {
            List<Complaint> complaints = complaintService.getComplaintsByComplaintType(type);
            return Result.success(complaints);
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            return Result.error("500", "按投诉类型查询投诉失败: " + e.getMessage());
        }
    }
}
