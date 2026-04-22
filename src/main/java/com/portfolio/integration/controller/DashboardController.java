package com.portfolio.integration.controller;

import com.portfolio.integration.domain.InterfaceChannelType;
import com.portfolio.integration.domain.InterfaceDirection;
import com.portfolio.integration.domain.InterfaceStatus;
import com.portfolio.integration.dto.ErrorLogSearchCondition;
import com.portfolio.integration.dto.InterfaceRegistrationRequest;
import com.portfolio.integration.dto.InterfaceSearchCondition;
import com.portfolio.integration.dto.InterfaceStatusUpdateRequest;
import com.portfolio.integration.dto.InterfaceUpdateRequest;
import com.portfolio.integration.service.InterfaceMonitoringService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DashboardController {

    private final InterfaceMonitoringService interfaceMonitoringService;

    public DashboardController(InterfaceMonitoringService interfaceMonitoringService) {
        this.interfaceMonitoringService = interfaceMonitoringService;
    }

    @GetMapping("/")
    public String dashboard(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false) InterfaceStatus status,
                            @RequestParam(required = false) InterfaceChannelType channelType,
                            @RequestParam(required = false) Boolean active,
                            @RequestParam(required = false) String logKeyword,
                            @RequestParam(required = false) Long logInterfaceId,
                            @RequestParam(required = false) Boolean retriable,
                            Model model) {
        model.addAttribute("metrics", interfaceMonitoringService.getDashboardMetrics());
        model.addAttribute("interfaces", interfaceMonitoringService.search(
                new InterfaceSearchCondition(keyword, status, channelType, active)
        ));
        model.addAttribute("errorLogs", interfaceMonitoringService.getErrorLogs(
                new ErrorLogSearchCondition(logKeyword, logInterfaceId, retriable)
        ));
        if (!model.containsAttribute("registrationForm")) {
            model.addAttribute("registrationForm", new InterfaceRegistrationRequest(
                    "", "", "", "", null, null, "", ""
            ));
        }
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("channelType", channelType);
        model.addAttribute("active", active);
        model.addAttribute("logKeyword", logKeyword);
        model.addAttribute("logInterfaceId", logInterfaceId);
        model.addAttribute("retriable", retriable);
        model.addAttribute("channelTypes", InterfaceChannelType.values());
        model.addAttribute("directions", InterfaceDirection.values());
        model.addAttribute("statuses", InterfaceStatus.values());
        return "dashboard";
    }

    @GetMapping("/interfaces/{id}")
    public String interfaceDetail(@PathVariable Long id, Model model) {
        var item = interfaceMonitoringService.getInterfaceSummary(id);
        model.addAttribute("item", item);
        model.addAttribute("updateForm", new InterfaceUpdateRequest(
                item.interfaceName(),
                item.sourceSystem(),
                item.targetSystem(),
                item.channelType(),
                item.direction(),
                item.status(),
                item.ownerTeam(),
                item.description(),
                item.active()
        ));
        model.addAttribute("statusForm", new InterfaceStatusUpdateRequest(item.status()));
        model.addAttribute("channelTypes", InterfaceChannelType.values());
        model.addAttribute("directions", InterfaceDirection.values());
        model.addAttribute("statuses", InterfaceStatus.values());
        model.addAttribute("logs", interfaceMonitoringService.getErrorLogs(
                new ErrorLogSearchCondition(null, id, null)
        ));
        return "interface-detail";
    }

    @PostMapping("/interfaces")
    public String registerInterface(@Valid @ModelAttribute("registrationForm") InterfaceRegistrationRequest request,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("metrics", interfaceMonitoringService.getDashboardMetrics());
            model.addAttribute("interfaces", interfaceMonitoringService.search(
                    new InterfaceSearchCondition(null, null, null, null)
            ));
            model.addAttribute("errorLogs", interfaceMonitoringService.getErrorLogs(
                    new ErrorLogSearchCondition(null, null, null)
            ));
            model.addAttribute("channelTypes", InterfaceChannelType.values());
            model.addAttribute("directions", InterfaceDirection.values());
            model.addAttribute("statuses", InterfaceStatus.values());
            return "dashboard";
        }

        interfaceMonitoringService.register(request);
        redirectAttributes.addFlashAttribute("message", "신규 인터페이스가 등록되었습니다.");
        return "redirect:/";
    }

    @PostMapping("/interfaces/{id}/update")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("updateForm") InterfaceUpdateRequest request,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("item", interfaceMonitoringService.getInterfaceSummary(id));
            model.addAttribute("statusForm", new InterfaceStatusUpdateRequest(request.status()));
            model.addAttribute("channelTypes", InterfaceChannelType.values());
            model.addAttribute("directions", InterfaceDirection.values());
            model.addAttribute("statuses", InterfaceStatus.values());
            model.addAttribute("logs", interfaceMonitoringService.getErrorLogs(
                    new ErrorLogSearchCondition(null, id, null)
            ));
            return "interface-detail";
        }

        interfaceMonitoringService.update(id, request);
        redirectAttributes.addFlashAttribute("message", "인터페이스 정보가 수정되었습니다.");
        return "redirect:/interfaces/" + id;
    }

    @PostMapping("/interfaces/{id}/status")
    public String changeStatus(@PathVariable Long id,
                               @Valid @ModelAttribute("statusForm") InterfaceStatusUpdateRequest request,
                               RedirectAttributes redirectAttributes) {
        interfaceMonitoringService.changeStatus(id, request);
        redirectAttributes.addFlashAttribute("message", "운영 상태가 변경되었습니다.");
        return "redirect:/interfaces/" + id;
    }

    @PostMapping("/interfaces/{id}/deactivate")
    public String deactivate(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        interfaceMonitoringService.deactivate(id);
        redirectAttributes.addFlashAttribute("message", "인터페이스가 비활성화되었습니다.");
        return "redirect:/";
    }

    @PostMapping("/interfaces/{id}/retry")
    public String retry(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        interfaceMonitoringService.retry(id);
        redirectAttributes.addFlashAttribute(
                "message",
                "실패 건 재처리를 수행했습니다."
        );
        return "redirect:/interfaces/" + id;
    }
}
