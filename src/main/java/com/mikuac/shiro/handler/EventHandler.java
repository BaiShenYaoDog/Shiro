package com.mikuac.shiro.handler;

import com.alibaba.fastjson.JSONObject;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.dto.event.notice.*;
import com.mikuac.shiro.dto.event.request.FriendAddRequestEvent;
import com.mikuac.shiro.dto.event.request.GroupAddRequestEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 事件处理器
 *
 * @author zero
 */
@Slf4j
@Component
public class EventHandler {

    BotPlugin defaultPlugin = new BotPlugin();

    @Resource
    private ApplicationContext applicationContext;

    /**
     * 事件处理器
     *
     * @param bot       bot对象
     * @param eventJson 响应数据
     */
    public void handle(Bot bot, JSONObject eventJson) {
        String postType = eventJson.getString("post_type");
        switch (postType) {
            case "meta_event" -> handleMetaEvent(bot, eventJson);
            case "message" -> handleMessage(bot, eventJson);
            case "notice" -> handleNotice(bot, eventJson);
            case "request" -> handleRequest(bot, eventJson);
            default -> defaultHandler();
        }
    }

    private void handleMessage(Bot bot, JSONObject eventJson) {
        String messageType = eventJson.getString("message_type");
        switch (messageType) {
            case "private" -> {
                PrivateMessageEvent event = eventJson.toJavaObject(PrivateMessageEvent.class);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onPrivateMessage(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
            }
            case "group" -> {
                GroupMessageEvent event = eventJson.toJavaObject(GroupMessageEvent.class);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onGroupMessage(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
            }
            default -> defaultHandler();
        }
    }

    private void handleNotice(Bot bot, JSONObject eventJson) {
        String noticeType = eventJson.getString("notice_type");
        switch (noticeType) {
            case "group_upload" -> {
                GroupUploadNoticeEvent event = eventJson.toJavaObject(GroupUploadNoticeEvent.class);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onGroupUploadNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
            }
            case "group_admin" -> {
                GroupAdminNoticeEvent event = eventJson.toJavaObject(GroupAdminNoticeEvent.class);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onGroupAdminNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
            }
            case "group_decrease" -> {
                GroupDecreaseNoticeEvent event = eventJson.toJavaObject(GroupDecreaseNoticeEvent.class);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onGroupDecreaseNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
            }
            case "group_increase" -> {
                GroupIncreaseNoticeEvent event = eventJson.toJavaObject(GroupIncreaseNoticeEvent.class);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onGroupIncreaseNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
            }
            case "group_ban" -> {
                GroupBanNoticeEvent event = eventJson.toJavaObject(GroupBanNoticeEvent.class);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onGroupBanNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
            }
            case "friend_add" -> {
                FriendAddNoticeEvent event = eventJson.toJavaObject(FriendAddNoticeEvent.class);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onFriendAddNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
            }
            case "group_recall" -> {
                GroupMsgDeleteNoticeEvent event = eventJson.toJavaObject(GroupMsgDeleteNoticeEvent.class);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onGroupMsgDeleteNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
            }
            case "friend_recall" -> {
                PrivateMsgDeleteNoticeEvent event = eventJson.toJavaObject(PrivateMsgDeleteNoticeEvent.class);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onPrivateMsgDeleteNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
            }
            case "group_card" -> {
                GroupCardChangeNotice event = eventJson.toJavaObject(GroupCardChangeNotice.class);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onGroupCardChangeNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
            }
            case "offline_file" -> {
                ReceiveOfflineFilesNoticeEvent event = eventJson.toJavaObject(ReceiveOfflineFilesNoticeEvent.class);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onReceiveOfflineFilesNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
            }
            case "notify" -> handleNotify(bot, eventJson);
            default -> defaultHandler();
        }
    }

    private void handleNotify(Bot bot, JSONObject eventJson) {
        String subType = eventJson.getString("sub_type");
        switch (subType) {
            case "poke" -> {
                PokeNoticeEvent event = eventJson.toJavaObject(PokeNoticeEvent.class);
                // 如果群号不为空则当作群内戳一戳处理
                if (event.getGroupId() > 0L) {
                    for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                        if (getPlugin(pluginClass).onGroupPokeNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                            break;
                        }
                    }
                } else {
                    for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                        if (getPlugin(pluginClass).onPrivatePokeNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                            break;
                        }
                    }
                }
            }
            case "lucky_king" -> {
                GroupLuckyKingNoticeEvent event = eventJson.toJavaObject(GroupLuckyKingNoticeEvent.class);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onGroupLuckyKingNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
            }
            case "honor" -> {
                GroupHonorChangeNoticeEvent event = eventJson.toJavaObject(GroupHonorChangeNoticeEvent.class);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onGroupHonorChangeNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
            }
            default -> defaultHandler();
        }
    }

    private void handleRequest(Bot bot, JSONObject eventJson) {
        String requestType = eventJson.getString("request_type");
        switch (requestType) {
            case "friend" -> {
                FriendAddRequestEvent event = eventJson.toJavaObject(FriendAddRequestEvent.class);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onFriendAddRequest(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
            }
            case "group" -> {
                GroupAddRequestEvent event = eventJson.toJavaObject(GroupAddRequestEvent.class);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onGroupAddRequest(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
            }
            default -> defaultHandler();
        }
    }

    private void handleMetaEvent(Bot bot, JSONObject eventJson) {
    }

    private void defaultHandler() {
    }

    private BotPlugin getPlugin(Class<? extends BotPlugin> pluginClass) {
        try {
            return applicationContext.getBean(pluginClass);
        } catch (Exception e) {
            log.warn("插件 {} 已被跳过，请检查 @Component 注解", pluginClass.getSimpleName());
            return defaultPlugin;
        }
    }

}