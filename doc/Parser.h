//
// Created by yangdr on 4/23/18.
//

#ifndef STREAM_CMD_PARSER
#define STREAM_CMD_PARSER

#include <unordered_map>
#include <list>
#include <iostream>
#include "Splitter.hpp"
#include "Rule.h"

namespace cmd {
    ///指令执行结果
    struct Result {
        ///指令执行状态
        enum class State : u_char {
            success,   //执行成功
            failure,   //执行异常
            error,     //无法匹配
            incomplete //指令不全
        };
        
        const State       what;  //怎么了
        const size_t      where; //在哪里
        const std::string info;  //指令执行信息 = 返回值
    
        ///指令执行结果
        Result(std::string info, bool success)
                : what(success ? State::success : State::failure),
                  where(0), info(std::move(info)) {}
    
        ///正确的反馈
        Result(const char *info)
                : what(State::success), where(0), info(info) {}
    
        ///错误信息
        Result(size_t index)
                : what(State::error), where(index), info("") {}
    
        ///指令不完整
        Result() : what(State::incomplete), where(0), info("") {}
    };
    
    using Examples = std::vector<std::string>;             ///示例库
    using Function = std::function<Result(SentenceRef)>;   ///执行器
    using Library  = std::unordered_map<size_t, Function>; ///函数库
    
    class Parser {
        std::vector<Rule> rules;
    
        inline Result parse(SentenceRef sentence) const noexcept {
            const auto rulesCount     = rules.size();    //规则总数
            const auto sentenceLength = sentence.size(); //句长
        
            std::vector<size_t> lengths(rulesCount);     //匹配长度缓存
            std::list<size_t>   successIndex;            //完全匹配规则的id
        
            for (size_t r = 0; r < rulesCount; ++r)      //遍历规则
                if (sentenceLength == (lengths[r] = rules[r][sentence])
                    &&                                   //整句匹配条件
                    sentenceLength == rules[r].dim())    //完全匹配条件
                    successIndex.push_back(r);           //压入列表
        
            if (successIndex.size() == 1) {              //唯一匹配[ok!]
                const auto id       = rules[successIndex.front()].id;
                const auto function = library.find(id);
                return library.end() == function
                       ? Result("no function bind id[" + std::to_string(id) + ']', false)
                       : function->second(sentence);
            }
            
            if (successIndex.size() > 1)                 //歧义匹配[...]
                return Result("there are more than one rules match this sentence, "
                              "please check your rules", false);
            
            const auto maxLength = std::max_element(lengths.begin(), lengths.end());
            return sentenceLength - 1 == *maxLength      //句子除了结束符全部匹配
                   &&                                    //不能匹配结束符是因为规则比句子长
                   sentenceLength < rules[maxLength - lengths.begin()].dim()
                   ? Result() : Result(*maxLength);
        }
    
    public:
        Library library;
    
        Parser() = default;
        
        Parser(const Parser &) = delete;
    
        Parser(Parser &&) = default;
    
        ///添加规则
        void operator+=(const std::string &example) {
            if (std::regex_match(example, std::regex(R"(^\s*$)"))) return;
            rules.emplace_back(Rule::build(example));
        }
    
        ///执行功能
        void operator()(const std::string &command) const {
            Sentence sentence = split(command);
            if (sentence.size() == 1) return;
            const auto result = parse(sentence);
            switch (result.what) {
                case Result::State::success:
                    std::cout << result.info << std::endl;
                    break;
                case Result::State::failure:
                    std::cerr << result.info << std::endl;
                    break;
                case Result::State::error:
                    std::cerr << "invalid command: ";
                    for (size_t i = 0; i < sentence.size() - 1; ++i)
                        if (i == result.where)
                            std::cerr << "> " << sentence[i].text << " < ";
                        else
                            std::cerr << sentence[i].text << ' ';
                    std::cerr << std::endl;
                    break;
                case Result::State::incomplete:
                    sentence.pop_back();
                    std::cerr << "incomplete command: ";
                    for (auto &token : sentence)
                        std::cerr << token.text << ' ';
                    std::cerr << "..." << std::endl;
                    break;
            }
        }
    };
}

#endif //STREAM_CMD_PARSER
