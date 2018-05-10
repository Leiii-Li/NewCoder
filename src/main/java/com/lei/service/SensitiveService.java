package com.lei.service;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by John on 2017/5/22.
 */
@Service
public class SensitiveService implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        // 在Service初始化时就将数创建
        String[] nodes = new String[]{"色情", "赌博", "卖淫", "吸毒", "反动派"};
        for (String node : nodes) {
            addTreeNode(node); // 每个node都是一个词组
        }
    }

    public String match(String txt) {
        if (StringUtils.isBlank(txt)) {
            return txt;
        }
        String replaceStr = "***";
        StringBuilder sb = new StringBuilder();
        TrieNode tempTrieNode = rootNode; // 敏感词组树
        int index = 0;
        int position = 0;
        // 只要position下标没有走到最后位置，说明还没有匹配完成，那么就需要继续往后匹配
        while (position < txt.length()) {
            char character = txt.charAt(position);
            TrieNode subNode = tempTrieNode.getSubNode(character);
            if (isSymbol(character)) {
                // 如果文字开头都是特殊字符，那么需要直接添加否则会丢失
                if (tempTrieNode == rootNode) {
                    sb.append(character);
                    ++index;
                }
                // 如果已发现敏感词，且中间夹杂着特殊字符，就不进行判断继续向后增加
                ++position;
                continue;
            }
            if (subNode == null) { //如果没有继续向后搜素
                position = index + 1;
                index = position;
                // 归0
                tempTrieNode = rootNode;
                sb.append(character);
                // 发现敏感词
            } else if (subNode.getIsKeyWordEnd()) {  //如果是敏感词词组那么将词组打码
                sb.append(replaceStr);
                index = position + 1;
                position = index;
                tempTrieNode = rootNode;
            } else {
                // 如果当前单词不为敏感词组的末尾词，那么position继续++  直到末尾词才进行替换 ，如果下一个词不为敏感词，那么第一个if为空，就认为不为敏感词
                tempTrieNode = subNode;
                ++position;
            }
        }
        sb.append(txt.substring(index)); // 将最后那个词添加至sb，且最后那个词不会是敏感词敏感词第二个if就被替换了
        return sb.toString();
    }

    // 判断是否是特殊字符
    private boolean isSymbol(char c) {
        int ic = (int) c;
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }

    private void addTreeNode(String node) {
        // 添加节点的思想： 将当前的字符在树上深度为一的节点上查找，如果没有找到，那么就直接添加进去，然后词组后面的单词就在这个新插入的节点上继续向下查找
        // 这种写法可以使 好人啊 好人  这两个敏感词汇都能被匹配出来
        TrieNode tempNode = rootNode;
        for (int i = 0; i < node.length(); ++i) {
            Character character = node.charAt(i);
            TrieNode subNode = tempNode.getSubNode(character);
            if (subNode == null) {
                subNode = new TrieNode();
                tempNode.addSubNode(character, subNode);
            }
            tempNode = subNode;
            if (i == node.length() - 1) {
                subNode.setIsKeyWordEnd();
            }
        }
    }

    private TrieNode rootNode = new TrieNode();

    public class TrieNode {
        private boolean end = false;
        private Map<Character, TrieNode> subNodes = new HashMap<Character, TrieNode>();

        public void setIsKeyWordEnd() {
            this.end = true;
        }

        public boolean getIsKeyWordEnd() {
            return end;
        }

        public TrieNode getSubNode(Character key) {
            return subNodes.get(key);
        }

        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }
    }
}
