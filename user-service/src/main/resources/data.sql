-- =================================================================
-- 1. 태그(Tags) 데이터 삽입
-- =================================================================
INSERT INTO tags (name, display_name, description, color, is_active) VALUES
                                                                         ('io', '입출력', '데이터를 읽고 출력하는 기본적인 문제입니다.', '#4285F4', true),
                                                                         ('math', '수학', '수학적 지식을 활용하여 해결하는 문제입니다.', '#EA4335', true),
                                                                         ('implementation', '구현', '문제에 제시된 로직을 그대로 코드로 옮겨야 하는 문제입니다.', '#FBBC05', true),
                                                                         ('array', '배열', '배열 자료구조를 사용하여 해결하는 문제입니다.', '#34A853', true),
                                                                         ('hash', '해시', '해시 테이블 또는 해시 맵을 사용하여 시간 복잡도를 개선하는 문제입니다.', '#9C27B0', true),
                                                                         ('recursion', '재귀', '재귀 함수를 사용하여 문제를 해결하는 방식입니다.', '#FF6F00', true);

-- =================================================================
-- 2. 문제(Problems) 데이터 삽입
-- =================================================================
-- 문제 1: A+B
INSERT INTO problems (number, title, desc_content, desc_input, desc_output, meta_category, created_at, updated_at) VALUES
    ('1000', 'A+B', '두 정수 A와 B를 입력받은 다음, A+B를 출력하는 프로그램을 작성하시오.', '첫째 줄에 A와 B가 공백으로 구분되어 주어집니다. (0 < A, B < 10)', '첫째 줄에 A+B를 출력합니다.', '입출력', NOW(), NOW());

-- 문제 2: 두 수의 합 (Two Sum)
INSERT INTO problems (number, title, desc_content, desc_input, desc_output, meta_category, created_at, updated_at) VALUES
    ('1001', '두 수의 합 (Two Sum)', '정수 배열 `nums`와 정수 `target`이 주어집니다. `nums` 배열에서 두 수를 더해 `target`이 되는 두 수의 **인덱스**를 찾아 반환하세요. 답은 항상 하나만 존재하며, 동일한 원소를 두 번 사용할 수 없습니다.', '첫째 줄에는 공백으로 구분된 정수 배열 `nums`가, 둘째 줄에는 정수 `target`이 주어집니다.', '두 수의 인덱스를 공백으로 구분하여 오름차순으로 출력합니다. (인덱스는 0부터 시작)', '배열/해시', NOW(), NOW());

-- 문제 3: 팩토리얼 (Factorial)
INSERT INTO problems (number, title, desc_content, desc_input, desc_output, meta_category, created_at, updated_at) VALUES
    ('1002', '팩토리얼', 'N이 주어졌을 때, N!을 계산하는 프로그램을 작성하시오. (N! = 1 × 2 × ... × N)', '첫째 줄에 정수 N(0 ≤ N ≤ 12)이 주어집니다.', '첫째 줄에 N!의 값을 출력합니다.', '수학/재귀', NOW(), NOW());

-- =================================================================
-- 3. 테스트 케이스(Test Cases) 데이터 삽입
-- =================================================================
-- 문제 1 (A+B) 테스트 케이스
INSERT INTO test_cases (problem_id, case_input, case_expected_output, case_is_public, case_time_limit_ms, case_memory_limit_mb) VALUES
                                                                                                                                    (1, '1 2', '3', true, 1000, 128),
                                                                                                                                    (1, '3 5', '8', false, 1000, 128),
                                                                                                                                    (1, '9 9', '18', false, 1000, 128);

-- 문제 2 (두 수의 합) 테스트 케이스
INSERT INTO test_cases (problem_id, case_input, case_expected_output, case_is_public, case_time_limit_ms, case_memory_limit_mb) VALUES
                                                                                                                                    (2, '2 7 11 15\n9', '0 1', true, 1000, 256),
                                                                                                                                    (2, '3 2 4\n6', '1 2', false, 1000, 256),
                                                                                                                                    (2, '3 3\n6', '0 1', false, 1000, 256);

-- 문제 3 (팩토리얼) 테스트 케이스
INSERT INTO test_cases (problem_id, case_input, case_expected_output, case_is_public, case_time_limit_ms, case_memory_limit_mb) VALUES
                                                                                                                                    (3, '5', '120', true, 1000, 128),
                                                                                                                                    (3, '0', '1', true, 1000, 128),
                                                                                                                                    (3, '12', '479001600', false, 1000, 128);

-- =================================================================
-- 4. 문제-태그 관계(Problem-Tags) 데이터 삽입
-- =================================================================
-- 문제 1 (A+B) 태그
INSERT INTO problem_tags (problem_id, tag_id) VALUES
                                                  (1, (SELECT _id FROM tags WHERE name = 'io')),
                                                  (1, (SELECT _id FROM tags WHERE name = 'implementation'));

-- 문제 2 (두 수의 합) 태그
INSERT INTO problem_tags (problem_id, tag_id) VALUES
                                                  (2, (SELECT _id FROM tags WHERE name = 'array')),
                                                  (2, (SELECT _id FROM tags WHERE name = 'hash'));

-- 문제 3 (팩토리얼) 태그
INSERT INTO problem_tags (problem_id, tag_id) VALUES
                                                  (3, (SELECT _id FROM tags WHERE name = 'math')),
                                                  (3, (SELECT _id FROM tags WHERE name = 'recursion'));