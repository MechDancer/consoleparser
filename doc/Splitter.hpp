//
// Created by yangdr on 4/22/18.
//

#ifndef STREAM_CMD_SPLITTER
#define STREAM_CMD_SPLITTER

#include <regex>
#include <mutex>

namespace cmd {
    ///标签类别
    enum class TokenType : u_char {
        integer, //整数
        number,  //有理数
        sign,    //符号
        word,    //词
        final,   //结束符
        note,    //插入注释
        key      //关键字（匹配非关键字或结束符的任意类型）
    };
    
    ///解析正则
    struct Regex {
        const std::regex Int       = std::regex(R"(^[-+]?\d+$)");
        const std::regex Hex       = std::regex(R"(^[-+]?0x[a-fA-F0-9]+$)");
        const std::regex Num       = std::regex(R"(^[-+]?\d*\.\d+$)");
        const std::regex Sign      = std::regex(R"(^[^a-zA-Z]$)");
        const std::regex note      = std::regex(R"(^\[-\D+-\]$)");
        const std::regex finalNote = std::regex(R"(^\[-\D+$)");
        const std::regex Key       = std::regex(R"(^\{(\D+)\}$)");
    };
    
    ///缓存访问
    const Regex &regex() {
        static Regex i;
        return i;
    }
    
    ///类别判定
    inline TokenType determine(const std::string &text) {
        std::cmatch matcher;
        return std::regex_match(text.data(), regex().Int)
               ||
               std::regex_match(text.data(), regex().Hex)
               ? TokenType::integer
               : std::regex_match(text.data(), regex().Num)
                 ? TokenType::number
                 : std::regex_match(text.data(), regex().note)
                   ? TokenType::note
                   : std::regex_match(text.data(), regex().finalNote)
                     ? TokenType::final
                     : std::regex_match(text.data(), matcher, regex().Key)
                       ? matcher[1].str() == "int"
                         ? TokenType::integer
                         : matcher[1].str() == "num"
                           ? TokenType::number
                           : matcher[1].str() == "word"
                             ? TokenType::word
                             : matcher[1].str() == "sign"
                               ? TokenType::sign
                               : TokenType::key
                       : text.size() == 1 && std::regex_match(text.data(), regex().Sign)
                         ? TokenType::sign
                         : TokenType::word;
    }
    
    ///词法分类
    struct Token {
        const std::string text; //字符
        const TokenType   type; //类别
        
        Token(std::string text, TokenType type)
                : text(std::move(text)), type(type) {}
        
        bool word() const { return type == TokenType::word; }
        
        bool final() const { return type == TokenType::final; }
        
        bool key() const { return type == TokenType::key; }
    };
    
    using Sentence    = std::vector<Token>;
    using SentenceRef = const Sentence &;
    
    /**
     * @param command 指令
     * @return 词组
     */
    std::vector<Token> split(const std::string &command) {
        std::vector<Token> result;
        std::stringstream  builder(command);
        std::string        text;
        while (builder >> text) {
            const auto type = determine(text);     //判定类别
            if (type == TokenType::note) continue; //插入注释
            if (type == TokenType::final) break;   //结束符
            result.emplace_back(text, type);       //其他
        }
        result.emplace_back("", TokenType::final);
        return result;
    }
}

#endif //STREAM_CMD_SPLITTER
