INSERT INTO insurance_interface
(interface_code, interface_name, source_system, target_system, channel_type, direction, status, success_count, failure_count, last_execution_at, owner_team, description, active)
VALUES
('IF-CLM-001', '보험금 청구 수신 연동', 'Partner Portal', 'Claim Core', 'REST_API', 'INBOUND', 'RUNNING', 1284, 12, DATEADD('MINUTE', -8, CURRENT_TIMESTAMP), '청구시스템팀', '제휴 채널에서 접수된 보험금 청구 데이터를 수신합니다.', TRUE),
('IF-POL-002', '증권 발행 배치 송신', 'Policy Core', 'Document Hub', 'BATCH', 'OUTBOUND', 'WARNING', 432, 9, DATEADD('HOUR', -2, CURRENT_TIMESTAMP), '계약운영팀', '매일 증권 발행 결과를 문서 시스템으로 배치 전송합니다.', TRUE),
('IF-PAY-003', '지급계 MQ 연계', 'Claim Core', 'Payment Gateway', 'MQ', 'BIDIRECTIONAL', 'FAILED', 820, 31, DATEADD('MINUTE', -21, CURRENT_TIMESTAMP), '지급정산팀', '보험금 지급요청 및 응답 메시지를 MQ로 송수신합니다.', TRUE),
('IF-CUS-004', '고객정보 동기화', 'CRM', 'Data Hub', 'SFTP', 'OUTBOUND', 'PAUSED', 221, 0, DATEADD('DAY', -1, CURRENT_TIMESTAMP), '고객플랫폼팀', '고객 마스터 데이터를 야간 배치로 동기화합니다.', TRUE),
('IF-UWD-005', '언더라이팅 심사 결과 송신', 'Underwriting Engine', 'Core Policy', 'SOAP', 'OUTBOUND', 'RUNNING', 912, 4, DATEADD('MINUTE', -33, CURRENT_TIMESTAMP), '상품심사팀', '계약 심사 결과와 리스크 판정값을 코어 시스템에 전달합니다.', TRUE);

INSERT INTO error_log
(interface_id, interface_code, message, detail, occurred_at, retriable)
VALUES
(3, 'IF-PAY-003', 'MQ ACK Timeout', 'Payment Gateway 응답 지연으로 ACK 수신에 실패했습니다.', DATEADD('MINUTE', -18, CURRENT_TIMESTAMP), TRUE),
(2, 'IF-POL-002', '배치 파일 스키마 경고', 'Document Hub 업로드 대상 CSV 필드 순서가 변경되었습니다.', DATEADD('HOUR', -3, CURRENT_TIMESTAMP), FALSE),
(3, 'IF-PAY-003', 'Retry Queue Overload', '재처리 큐 적체로 처리 지연이 발생했습니다.', DATEADD('MINUTE', -55, CURRENT_TIMESTAMP), TRUE);

INSERT INTO interface_execution_history
(interface_id, interface_code, status, result_type, message, executed_at, retried)
VALUES
(1, 'IF-CLM-001', 'RUNNING', 'SUCCESS', '청구 수신 처리 정상 완료', DATEADD('MINUTE', -6, CURRENT_TIMESTAMP), FALSE),
(2, 'IF-POL-002', 'WARNING', 'WARNING', '배치 검증 경고 발생', DATEADD('MINUTE', -20, CURRENT_TIMESTAMP), FALSE),
(3, 'IF-PAY-003', 'FAILED', 'FAILURE', 'MQ ACK Timeout 재발생', DATEADD('MINUTE', -18, CURRENT_TIMESTAMP), FALSE),
(4, 'IF-CUS-004', 'PAUSED', 'WARNING', '야간 동기화 일시중지 상태 유지', DATEADD('HOUR', -4, CURRENT_TIMESTAMP), FALSE),
(5, 'IF-UWD-005', 'RUNNING', 'SUCCESS', '심사 결과 송신 정상 완료', DATEADD('MINUTE', -11, CURRENT_TIMESTAMP), FALSE);
