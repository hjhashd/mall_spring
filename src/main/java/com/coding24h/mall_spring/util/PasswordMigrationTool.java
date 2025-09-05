
package com.coding24h.mall_spring.util;

import com.coding24h.mall_spring.entity.User;
import com.coding24h.mall_spring.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Component
public class PasswordMigrationTool implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder; // 直接使用PasswordEncoder

    @Autowired
    private PlatformTransactionManager transactionManager; // 添加事务管理器

    @Override
    public void run(String... args) throws Exception {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(status -> {
            migratePasswords();
            return null;
        });

        // 添加显式提交（确保事务提交）
        transactionManager.commit(transactionTemplate.getTransactionManager().getTransaction(null));
    }


    private void migratePasswords() {
        List<User> users = userMapper.findAllUsersToChange();
        int migratedCount = 0;
        int skippedCount = 0;
        int errorCount = 0;

        for (User user : users) {
            try {
                String password = user.getPassword();

                // 更健壮的加密密码检测
                if (password == null || password.isEmpty()) {
                    skippedCount++;
                    continue;
                }

                // 检查密码是否已加密
                if (password.startsWith("$2a$") ||
                        password.startsWith("$2b$") ||
                        password.startsWith("$2y$")) {
                    skippedCount++;
                    continue;
                }

                System.out.println("迁移用户: " + user.getUsername());
                System.out.println("原密码: " + password);

                // 加密现有密码
                String encryptedPassword = passwordEncoder.encode(password);
                System.out.println("加密后: " + encryptedPassword);

                // 更新密码
                userMapper.updateOldPassword(user.getUserId(), encryptedPassword);
                migratedCount++;

            } catch (Exception e) {
                System.err.println("迁移用户 " + user.getUsername() + " 失败: " + e.getMessage());
                errorCount++;
            }
        }

        System.out.println("密码迁移报告:");
        System.out.println("总用户数: " + users.size());
        System.out.println("已迁移: " + migratedCount);
        System.out.println("已跳过: " + skippedCount);
        System.out.println("失败: " + errorCount);
    }
}