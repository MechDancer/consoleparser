//
// Created by yangdr on 18-4-23.
//

#ifndef STREAM_CMD_RULE
#define STREAM_CMD_RULE

#include "Splitter.hpp"

namespace cmd {
    struct Rule {
    private:
        Sentence example{};
    
    public:
        const size_t id;
    
        Rule(size_t id, Sentence &&sentence)
                : id(id), example(std::forward<Sentence>(sentence)) {}
    
        ///生成器
        static inline Rule build(const std::string &example) {
            std::cmatch matcher;
            std::regex_match(example.data(), matcher, std::regex(R"(^#(\d+):\s*([\s|\S]+)$)"));
            if (matcher.size() != 3) //[整体][id][body]
                throw std::runtime_error("failed to build a rule by example: " + example);
            return {std::stoul(matcher[1].str()), split(matcher[2].str())};
        }
        
        /**
         * 匹配
         * @param sentence 句子
         * @return 匹配长度
         */
        size_t operator[](const Sentence &sentence) const {
            for (size_t i = 0; i < sentence.size(); ++i) {
                switch (example[i].type) {
                    case TokenType::word:
                    case TokenType::sign: {
                        const bool typeMismatch = sentence[i].type != example[i].type,
                                   textMismatch = sentence[i].text != example[i].text,
                                   notKey       = !std::regex_match(example[i].text.data(), regex().Key);
                        if (typeMismatch || (textMismatch && notKey))
                            return i;
                    }
                        break;
                    case TokenType::key:
                        if (sentence[i].key() || sentence[i].final())
                            return i;
                        break;
                    case TokenType::integer:
                    case TokenType::number:
                    case TokenType::final:
                        if (sentence[i].type != example[i].type)
                            return i;
                        break;
                    case TokenType::note:
                        throw std::invalid_argument("note appear in a sentence");
                }
            }
            return sentence.size();
        }
    
        size_t dim() const { return example.size(); }
    };
}

#endif //STREAM_CMD_RULE
