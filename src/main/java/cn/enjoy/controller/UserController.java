package cn.enjoy.controller;

import cn.enjoy.es.EnjoyUserIndex;
import cn.enjoy.es.UserRepository;
import cn.enjoy.service.IUserService;
import org.elasticsearch.index.query.DisMaxQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@RestController
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Resource
    private IUserService iUserService;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/hello")
    public Object sayHello() {
        logger.debug("这是个hello的日志");
        return "hello2";
    }

    @RequestMapping("/login")
    public String login(String username,String passwd) {
        boolean login = iUserService.login(username, passwd);
        if(login) {
            return "登陆成功";
        }else {
            return  "登陆失败";
        }
    }

    @RequestMapping("/register")
    public String register(String username,String passwd) {
        boolean login = iUserService.register(username, passwd);
        if(login) {
            return "注册成功";
        }else {
            return  "注册失败";
        }
    }

    @RequestMapping("/batchAdd")
    public String batchAdd(String username,String passwd) {
        iUserService.batchAdd(username, passwd);
        return "成功";
    }
    @PostMapping("/add")
    public String add(@RequestBody EnjoyUserIndex userIndex){
        userRepository.save(userIndex);
        return "success";
    }

    @GetMapping("/get")
    public String getAll() {
        Iterable<EnjoyUserIndex> iterable = userRepository.findAll();
        List<EnjoyUserIndex> list = new ArrayList<>();
        iterable.forEach(list::add);
        String re = "";
        for (EnjoyUserIndex s : list){
            re+=s.getUsername()+",";
        }
        return re;
    }

    @GetMapping("/findLike")
    public List<EnjoyUserIndex> findLike(String username){
        Pageable page = new PageRequest(0,10);
        Page<EnjoyUserIndex> userPage = userRepository.findByUsernameLike(username, page);
        List<EnjoyUserIndex> content = userPage.getContent();
        return content;

    }

    @GetMapping("/findAll")
    public List<EnjoyUserIndex> findAll(String username){
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(structureQuery(username)).build();
        Page<EnjoyUserIndex> search = userRepository.search(searchQuery);
        List<EnjoyUserIndex> content = search.getContent();
        return content;
    }

    /**
     * 中文、拼音混合搜索
     *
     * @param content the content
     * @return dis max query builder
     */
    public DisMaxQueryBuilder structureQuery(String content) {
        //使用dis_max直接取多个query中，分数最高的那一个query的分数即可
        DisMaxQueryBuilder disMaxQueryBuilder = QueryBuilders.disMaxQuery();
        //boost 设置权重,只搜索匹配name和disrector字段
        QueryBuilder ikNameQuery = QueryBuilders.matchQuery("username", content).boost(2f);
        QueryBuilder pinyinNameQuery = QueryBuilders.matchQuery("username.pinyin", content);
        disMaxQueryBuilder.add(ikNameQuery);
        disMaxQueryBuilder.add(pinyinNameQuery);
        return disMaxQueryBuilder;
    }

}
