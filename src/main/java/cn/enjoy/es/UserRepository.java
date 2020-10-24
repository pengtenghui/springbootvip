package cn.enjoy.es;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserRepository extends ElasticsearchRepository<EnjoyUserIndex,String> {

    Page<EnjoyUserIndex> findByUsernameLike(String username, Pageable page);
}
