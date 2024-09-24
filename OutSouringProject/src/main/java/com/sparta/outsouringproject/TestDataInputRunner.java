//package com.sparta.outsouringproject;
//
//import com.sparta.outsouringproject.cart.entity.Cart;
//import com.sparta.outsouringproject.cart.repository.CartItemRepository;
//import com.sparta.outsouringproject.cart.repository.CartRepository;
//import com.sparta.outsouringproject.menu.entity.Menu;
//import com.sparta.outsouringproject.menu.repository.MenuRepository;
//import com.sparta.outsouringproject.order.repository.OrderItemRepository;
//import com.sparta.outsouringproject.order.repository.OrderRepository;
//import com.sparta.outsouringproject.review.repository.ReviewRepository;
//import com.sparta.outsouringproject.store.dto.CreateStoreRequestDto;
//import com.sparta.outsouringproject.store.entity.Store;
//import com.sparta.outsouringproject.store.repository.StoreRepository;
//import com.sparta.outsouringproject.user.config.PasswordEncoder;
//import com.sparta.outsouringproject.user.entity.Role;
//import com.sparta.outsouringproject.user.entity.User;
//import com.sparta.outsouringproject.user.repository.UserRepository;
//import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//import java.util.stream.Collectors;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class TestDataInputRunner implements ApplicationRunner {
//
//    private final PasswordEncoder passwordEncoder;
//    private final UserRepository userRepository;
//    private final StoreRepository storeRepository;
//    private final MenuRepository menuRepository;
//    private final OrderItemRepository orderItemRepository;
//    private final CartRepository cartRepository;
//    private final CartItemRepository cartItemRepository;
//    private final OrderRepository orderRepository;
//    private final ReviewRepository reviewRepository;
//
//    @Override
//    @Transactional
//    public void run(ApplicationArguments args) throws Exception {
//        List<Long> userIds = createUsers(1000);
//        List<Long> ownerIds = createOwners(50);
//        List<Long> storeIds = createStores(ownerIds);
//        createMenu(storeIds, 30);
//    }
//
//    @Transactional
//    public List<Long> createUsers(int count) {
//        List<User> users = new ArrayList<>();
//        List<Cart> carts = new ArrayList<>();
//        for(int i = 0; i < count; ++i){
//            String password = "AAbb!@#$" + (i + 1); // AAbb!@#$%1 ~ AAbb!@#$%1000
//            String username = "name_" + (i + 1); // name_1 ~ name_1000
//            String email = "aaaa" + (i + 1) + "@gmail.com"; // aaaa1@gmail.com ~ aaaa1000@gmail.com
//            User user = new User(passwordEncoder.encode(password), username, email, Role.USER, false);
//            users.add(user);
//            Cart cart = new Cart(user);
//            carts.add(cart);
//        }
//        users = userRepository.saveAll(users);
//        cartRepository.saveAll(carts);
//        List<Long> collect = users.stream()
//            .map(x -> x.getId())
//            .toList();
//
//        return collect;
//    }
//
//    @Transactional
//    public List<Long> createOwners(int count) {
//        List<User> owners = new ArrayList<>();
//        List<Cart> carts = new ArrayList<>();
//        for(int i = 0; i < count; ++i){
//            String password = "AAbb!@#$" + (i + 1); // AAbb!@#$1 ~ AAbb!@#$1000
//            String username = "name_" + (i + 1); // name_1 ~ name_1000
//            String email = "owner" + (i + 1) + "@gmail.com"; // aaaa1@gmail.com ~ aaaa1000@gmail.com
//            User user = new User(passwordEncoder.encode(password), username, email, Role.OWNER, false);
//            owners.add(user);
//            carts.add(new Cart(user));
//        }
//
//        owners = userRepository.saveAll(owners);
//        carts = cartRepository.saveAll(carts);
//
//        List<Long> collect = owners.stream()
//            .map(x -> x.getId())
//            .toList();
//
//        return collect;
//    }
//
//    @Transactional
//    public List<Long> createStores(List<Long> ownerIds){
//        List<Store> stores = new ArrayList<>();
//        for(int i = 0; i < ownerIds.size(); ++i){
//            Long l = ownerIds.get(i);
//            User owner = userRepository.findById(l)
//                .get();
//            String storeName = "store_name_" + (i + 1);
//            double p = (double)(10000 + (int)(Math.random() * 20000));
//            int open_h = 6 + (int)(Math.random() * 5);
//            LocalTime open = LocalTime.of(open_h, 0);
//            LocalTime close = LocalTime.of(23, 0);
//            CreateStoreRequestDto createStoreRequestDto = CreateStoreRequestDto.builder()
//                .name(storeName)
//                .orderAmount(p)
//                .openTime(open)
//                .closeTime(close)
//                .build();
//            stores.add(new Store(createStoreRequestDto, owner));
//        }
//
//        stores = storeRepository.saveAll(stores);
//        return stores.stream().map(x->x.getStoreId()).toList();
//    }
//
//    @Transactional
//    public void createMenu(List<Long> storeIds, int maxMenuCount){
//        List<Menu> menus = new ArrayList<>();
//        Random random = new Random();
//        for(Long id : storeIds){
//            Store store = storeRepository.findById(id).get();
//            int menuCount = random.nextInt(2, maxMenuCount + 1);
//            for(int i = 0; i < menuCount; ++i){
//                String menuName = "store_"+ id.longValue() + "_" +"menu_name_" + (menuCount + 1);
//                long price = 3000 + (long)(Math.random() * 20000);
//                Menu menu = new Menu("adsf", null, price);
//                menu.relatedStore(store);
//                menus.add(menu);
//            }
//        }
//        menus = menuRepository.saveAll(menus);
//    }
//}
