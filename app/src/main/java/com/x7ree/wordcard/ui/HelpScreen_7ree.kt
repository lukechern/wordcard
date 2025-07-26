package com.x7ree.wordcard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.net.Uri
import com.x7ree.wordcard.utils.AppVersionUtils_7ree

@Composable
fun HelpScreen_7ree() {
    val context = LocalContext.current
    val currentVersion = AppVersionUtils_7ree.getFormattedVersion(context)
    val scrollState = rememberScrollState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 32.dp) // 右侧留更多空间给滚动条
        ) {
        Text(
            text = "📚 WordCard 使用指南",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        Text(
            text = "欢迎来到 单词卡片WordCard 的奇妙世界！🎉 这里有一份贴心的使用指南，让你快速上手这个超棒的单词学习神器～",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        HelpStep_7ree(
            stepNumber = "第一步",
            icon = Icons.Filled.Settings,
            title = "安装配置，一步到位！",
            description = "首先安装 APK 文件（这个你肯定已经搞定了，不然怎么看到这个页面呢？😄）。然后点击底部导航栏的【仪表盘】，再点击右上角的齿轮图标⚙️，进入配置页面。在这里配置你的大模型 API 参数——只要是兼容 OpenAI 格式的都可以哦！其他配置项保持默认就行，我们已经帮你调好了最佳参数。"
        )
        
        HelpStep_7ree(
            stepNumber = "第二步",
            icon = Icons.Filled.Phone,
            title = "桌面小组件",
            description = "在手机桌面长按空白处，选择【小组件】，找到 单词卡片WordCard 并添加到桌面。以后想查单词时，直接点击小组件就能弹出查询卡片，超级方便！再也不用打开 APP 了～"
        )
        
        HelpStep_7ree(
            stepNumber = "第三步",
            icon = Icons.Filled.Star,
            title = "开始学习之旅！",
            description = "遇到生词？直接在小组件里输入就行！我们的 AI 大模型会瞬间为你解释单词含义，还会贴心地提供例句帮助理解。更棒的是，还有真人发音朗读功能，让你的发音也能更标准！"
        )
        
        HelpStep_7ree(
            stepNumber = "第四步",
            icon = Icons.Filled.Book,
            title = "单词本，你的专属词汇宝库",
            description = "所有查过的单词都会自动保存到单词本里，再也不怕忘记了！在单词本里可以随时复习，点击单词右边的小喇叭🔊就能听发音，点击单词本身还能进入详情页查看更多信息。"
        )
        
        HelpStep_7ree(
            stepNumber = "第五步",
            icon = Icons.Filled.CheckCircle,
            title = "单词详情页，深度学习好帮手",
            description = "在单词详情页里，你可以让 APP 朗读单词，收藏重要词汇，还能通过上下滑动手指来切换其他单词进行复习。更棒的是，现在还新增了拼写练习功能！点击拼写练习卡片，就能开始单词拼写训练，界面会以深绿色大标题显示中文词义（如果词义较多会智能显示前两个），让你在理解词义的基础上练习拼写。这样的学习体验，是不是很棒？"
        )
        
        HelpStep_7ree(
            stepNumber = "第六步",
            icon = Icons.Filled.Dashboard,
            title = "仪表盘，见证你的成长",
            description = "想看看自己的学习成果？进入仪表盘就能查看详细的学习数据和统计图表。看着那些不断增长的数字，成就感满满！📈"
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.Gray.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "🎯 小贴士",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "记住，学习语言最重要的是坚持！每天查几个生词，积少成多，你的词汇量会在不知不觉中突飞猛进。单词卡片WordCard 会一直陪伴你的学习之路，加油！💪",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // MIT 协议说明
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.Gray.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "📜 开源协议",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "本应用采用 MIT 开源协议 🎉 这意味着什么呢？简单来说就是：你可以随意使用、修改、分发这个应用，甚至用来做商业项目都没问题！唯一的要求就是保留原作者的版权声明。MIT 协议就像是软件界的 \"随便用\" 许可证，既保护了开发者的权益，又给了用户最大的自由度。开源让世界更美好！🌍✨",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 致谢部分
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.Gray.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "🙏 特别致谢",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Text(
                text = "感谢所有为这个项目提供帮助的 AI 大模型朋友们：",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "🤖 OpenAI GPT 系列 - 提供强大的语言理解和生成能力，让单词解释更加准确生动\n" +
                        "🧠 Claude 系列 - 在代码优化和逻辑梳理方面提供了宝贵建议\n" +
                        "🌟 通义千问 - 在中文本地化和用户体验优化方面贡献良多\n" +
                        "🚀 Gemini - 在技术架构和性能优化方面给出了专业指导\n" +
                        "💎 deepseek - 在长文本处理和上下文理解方面表现出色",
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 18.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Text(
                text = "没有这些 AI 伙伴的帮助，WordCard 不可能达到今天的水平。人工智能与人类智慧的结合，创造出了更好的学习工具。向所有参与开源社区建设的开发者们致敬！🎊",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 联系我们部分
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.Gray.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "📞 联系我们",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Text(
                text = "欢迎来到 WordCard 的开源世界！🌟",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "🔗 GitHub 仓库地址：",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            val githubUrl = "https://github.com/lukechern/wordcard"
            val annotatedString = buildAnnotatedString {
                withLink(
                    LinkAnnotation.Url(githubUrl)
                ) {
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.Medium
                        )
                    ) {
                        append(githubUrl)
                    }
                }
            }
            
            Text(
                text = annotatedString,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Text(
                text = "在这里你可以：\n" +
                        "⭐ 给项目点个星星，支持开发者\n" +
                        "🐛 报告 Bug 或提出改进建议\n" +
                        "🔧 参与代码贡献，一起完善项目\n" +
                        "📖 查看详细的开发文档和更新日志\n" +
                        "� 与其他用户和开发者交流讨论出",
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "开源项目的成长离不开每一位用户的支持！无论是使用反馈、Bug 报告还是代码贡献，都是对我们最大的鼓励。让我们一起打造更好的单词学习工具！🚀",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 版本信息卡片
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.Gray.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "📱 版本信息",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // 修改为左对齐，并将"当前版本"和版本号放在同一行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "当前版本",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = currentVersion,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Android 兼容的滚动指示器
        if (scrollState.maxValue > 0) {
            BoxWithConstraints(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxHeight()
                    .width(12.dp)
                    .padding(end = 2.dp, top = 8.dp, bottom = 8.dp)
                    .background(
                        color = Color.Gray.copy(alpha = 0.11f),
                        shape = RoundedCornerShape(3.dp)
                    )
            ) {
                val trackHeight = maxHeight
                
                // 计算可见区域与总内容的比例
                val viewportHeight = scrollState.viewportSize.toFloat()
                val contentHeight = scrollState.maxValue.toFloat() + viewportHeight
                val thumbHeightRatio = (viewportHeight / contentHeight).coerceIn(0.1f, 1f)
                
                // 计算滚动进度
                val scrollProgress = if (scrollState.maxValue > 0) {
                    scrollState.value.toFloat() / scrollState.maxValue.toFloat()
                } else 0f
                
                // 计算拇指位置 - 基于实际轨道高度
                val thumbHeight = trackHeight * thumbHeightRatio
                val availableSpace = trackHeight - thumbHeight
                val thumbOffset = availableSpace * scrollProgress
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(thumbHeight)
                        .offset(y = thumbOffset)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(3.dp)
                        )
                )
            }
        }
    }
}

@Composable
private fun HelpStep_7ree(
    stepNumber: String,
    icon: ImageVector,
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.Gray.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
            .padding(bottom = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 8.dp)
            )
            Text(
                text = stepNumber,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 20.sp
        )
    }
    
    Spacer(modifier = Modifier.height(16.dp))
}