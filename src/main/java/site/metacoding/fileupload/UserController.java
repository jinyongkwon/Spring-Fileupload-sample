package site.metacoding.fileupload;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @GetMapping("/main")
    public String main(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("user", users.get(0));
        // User user = userRepository.findById(1).get();
        return "main";
    }

    @PostMapping("/join")
    public String join(JoinDto joinDto) { // 버퍼로 읽는거 1. json을 받을때 2. 있는 그대로 받고 싶을때
        UUID uuid = UUID.randomUUID();
        String requestFileName = joinDto.getFile().getOriginalFilename();
        System.out.println("전송받은 파일 명 : " + requestFileName);
        String imgurl = uuid + "_" + requestFileName;
        // 메모리에 있는 파일 데이터를 파일시스템으로 옮겨야 함
        // 1. 빈 파일 생성 ex)haha.png
        // File file = new File("d:\\example\\file.txt");

        // 2. 빈 파일에 스트림 연결
        // 3. 파일의 크기를 알기 때문에 for문 돌리면서 바이트로 쓰면 됨 . FIle Writer 객체!!
        // => 파일의 크기를 모르기 때문에 buffer를 사용하는데 크기를 알기때문에 사용할 필요 X

        try {
            // 주의!!
            // 1. 폴더가 이미 만들어져 있어야함
            // 2. 리눅스 / 사용하고, 윈도우 \ 사용!!
            // img Url = a.png
            // 3. 윈도우 : c:/upload/ 4. 리눅스 : /upload/
            // 우리는 상대경로 사용할 예정
            // 배포하기 위해 jar파일로 구우면 안돌아간다
            Path filePath = Paths.get("src/main/resources/static/upload/" + imgurl); // 보통 /를 사용하고 독자적으로 알아서
                                                                                     // 운영체제에 맞게
            // 찾아주지만,
            System.out.println(filePath);
            // 몇몇 라이브러리는 자기가 직접 운영체제에 맞게 경로를 찾아야한다.
            Files.write(filePath, joinDto.getFile().getBytes()); // 파일의 경로와 파일의 크기를 넣음.
            userRepository.save(joinDto.toEntity(imgurl));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "joinComplete"; // ViewResolver
    }
}
