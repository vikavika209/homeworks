package person.data.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SetToPageService {

    public static <T> Page<T> convertSetToPage(Set<T> set, Pageable pageable) {
        List<T> list = new ArrayList<>(set);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());

        List<T> pagedList = list.subList(start, end);

        return new PageImpl<>(pagedList, pageable, list.size());
    }
}
