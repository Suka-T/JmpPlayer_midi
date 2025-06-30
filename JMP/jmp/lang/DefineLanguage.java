package jmp.lang;

public class DefineLanguage {
    public static final String CODE_LINE_END = "@@LINE_END@@";
    public static final int INDEX_LANG_ENGLISH = 0;
    public static final int INDEX_LANG_JAPANESE = 1;
    public static final int INDEX_LANG_CHINESE = 2;
    public static final int INDEX_LANG_TRADITIONALCHINESE = 3;
    public static final int INDEX_LANG_KOREAN = 4;
    public static final int INDEX_LANG_RUSSIAN = 5;
    public static final int NUMBER_OF_INDEX_LANG = 6;
    static final LangID[] titles = new LangID[] { LangID.English, LangID.Japanese, LangID.Chinese, LangID.TraditionalChinese, LangID.Korean, LangID.Russian, };
    static final String[] langCodes = new String[] { "en", "ja", "cn", "tw", "ko", "ru", };

    public static enum LangID {//
        Language, //
        Japanese, //
        English, //
        Korean, //
        Chinese, //
        TraditionalChinese, //
        Russian, //
        File, //
        Save, //
        Open, //
        Reload, //
        Version, //
        Exit, //
        Window, //
        Allways_on_top, //
        Player, //
        Loop_playback, //
        Continuous_playback, //
        Playlist, //
        History, //
        Plugin, //
        Add_plugin, //
        Remove_plugin, //
        Close_all_plugin, //
        Setting, //
        MIDI_settings, //
        MIDI_device_settings, //
        Open_MIDI_device_settings_on_startup, //
        PC_reset, //
        MIDI_message_monitor, //
        MIDI_message_sender, //
        Now_loading, //
        Plugin_error, //
        FILE_ERROR_1, //
        FILE_ERROR_2, //
        FILE_ERROR_3, //
        FILE_ERROR_4, //
        FILE_ERROR_5, //
        FILE_ERROR_6, //
        FILE_LOAD_SUCCESS, //
        PLUGIN_LOAD_SUCCESS, //
        PLUGIN_LOAD_ERROR, //
        Clear, //
        Playback, //
        Stop, //
        Fast_forward, //
        Rewind, //
        Next, //
        Previous, //
        Close, //
        Add, //
        Remove, //
        Open_with_Explorer, //
        MIDI_Data_byte, //
        Send, //
        Invalid_byte_data, //
        License, //
        Above_conditions, //
        Accept, //
        Reject, //
        Copied_to_clipboard, //
        Original_copy, //
        Common_settings, //
        Apply, //
        Error, //
        Message, //
        Layout_initialization, //
        Tool, //
        FFmpeg_converter, //
        FFmpeg_path, //
        Output_directory, //
        Input_file, //
        Play_after_convert, //
        Convert, //
        A_is_invalid, //
        Conversion_failed, //
        Conversion_completed, //
        Now_converting, //
        Initialize_setting, //
        Leave_output_file, //
        Use_FFmpeg_player, //
        Automatically_assign_MIDI_channel, //
        Automatically_assign_Program_change_number, //
        Confirm_jms_with_different_versions, //
        Confirm_jmz_with_different_versions, //
        D_and_D_the_playback_file_here, //
        Plugin_manager, //
        Lyrics_display, //
        Send_system_setup_before_playback, //
        Whether_to_display_every_time_at_startup, //
        Play_from_the_beginning, //
        Send_system_setup, //
        Automatic_selection, //
        Builtin_synthesizer, //
        Builtin_synthesizer_settings, //
        An_unexpected_error_has_occurred, //
        Exit_the_application, //
        There_was_a_problem_preparing_the_music_player, //
        Failed_to_initialize_the_application, //
        The_application_could_not_be_terminated_successfully, //
        Dont_choose_a_synthesizer, //
        Random_playback, //
        Allow_window_size_change, //
        Load, //
        Midi_File, //
        Wav_File, //
        Synchronized_MIDI_and_WAV_play,//
    }//

    static LangMap langMap = new LangMap() {
        {
            put(LangID.Language, new LanguageWords("Language", "言語", "语言", "語", "언어", "Язык"));
            put(LangID.Japanese, new LanguageWords("Japanese", "日本語", "日本語", "日本人", "日本語", "японский язык"));
            put(LangID.English, new LanguageWords("English", "English", "English", "英語", "English", "английский"));
            put(LangID.Korean, new LanguageWords("Korean", "한국어", "한국어", "韓國人", "한국어", "корейский язык"));
            put(LangID.Chinese, new LanguageWords("Chinese", "中文", "中文", "中國人", "中文", "китайский язык"));
            put(LangID.TraditionalChinese, new LanguageWords("Traditional Chinese", "繁體中文", "繁體中文", "繁體中文", "繁體中文", "繁體中文"));
            put(LangID.Russian, new LanguageWords("Russian", "русский", "русский", "俄語", "русский", "русский"));
            put(LangID.File, new LanguageWords("File", "ファイル", "文件", "文件", "파일", "Файл"));
            put(LangID.Save, new LanguageWords("Save", "保存", "保存", "保存", "구하다", "Сохранять"));
            put(LangID.Open, new LanguageWords("Open", "開く", "打开", "打開", "열려있는", "Открытым"));
            put(LangID.Reload, new LanguageWords("Reload", "リロード", "重装", "重新加載", "새로고침", "Перезагрузить"));
            put(LangID.Version, new LanguageWords("Version", "バージョン情報", "版", "版本", "버전", "Версия"));
            put(LangID.Exit, new LanguageWords("Exit", "終了", "出口", "出口", "출구", "Выход"));
            put(LangID.Window, new LanguageWords("Window", "ウィンドウ", "窗口", "窗戶", "창문", "Окно"));
            put(LangID.Allways_on_top, new LanguageWords("Allways on top", "常に手前に表示", "总在最前面", "始終在上面", "항상 위에", "Всегда на высоте"));
            put(LangID.Player, new LanguageWords("Player", "プレイヤー", "播放器", "播放器", "플레이어", "Игрок"));
            put(LangID.Loop_playback, new LanguageWords("Loop playback", "ループ再生", "循环播放", "循環播放", "루프 재생", "Циклическое воспроизведение"));
            put(LangID.Continuous_playback, new LanguageWords("Continuous playback", "連続再生", "连续播放", "連續播放", "연속 재생", "Непрерывное воспроизведение"));
            put(LangID.Playlist, new LanguageWords("Playlist", "プレイリスト", "播放清单", "播放列表", "재생 목록", "Плейлист"));
            put(LangID.History, new LanguageWords("History", "履歴", "历史", "歷史", "역사", "История"));
            put(LangID.Plugin, new LanguageWords("Plugin", "プラグイン", "插入", "插入", "플러그인", "Плагин"));
            put(LangID.Add_plugin, new LanguageWords("Add plugin", "プラグインを追加", "添加插件", "添加插件", "플러그인 추가", "Добавить плагин"));
            put(LangID.Remove_plugin, new LanguageWords("Remove plugin", "プラグインを削除", "删除插件", "刪除插件", "플러그인 제거", "Удалить плагин"));
            put(LangID.Close_all_plugin, new LanguageWords("Close all plugin", "すべて閉じる", "关闭所有插件", "關閉所有插件", "모든 플러그인 닫기", "Закройте все плагины"));
            put(LangID.Setting, new LanguageWords("Setting", "設定", "设置", "環境", "환경", "Параметр"));
            put(LangID.MIDI_settings, new LanguageWords("MIDI Settings", "MIDI設定", "MIDI设定", "MIDI 設置", "MIDI 설정", "Настройки MIDI"));
            put(LangID.MIDI_device_settings,
                    new LanguageWords("MIDI Device settings", "MIDIデバイス設定", "MIDI设备设置", "MIDI 設備設置", "MIDI 장치 설정", "Настройки MIDI-устройства"));
            put(LangID.Open_MIDI_device_settings_on_startup, new LanguageWords("Open MIDI device settings on startup", "起動時にMIDIデバイス設定を開く", "启动时打开MIDI设备设置",
                    "啟動時打開 MIDI 設備設置", "시작 시 MIDI 장치 설정 열기", "Открывать настройки MIDI-устройства при запуске"));
            put(LangID.PC_reset, new LanguageWords("Program change reset", "プログラムチェンジリセット", "程序变更重置", "程序更改重置", "프로그램 변경 리셋", "Сброс изменения программы"));
            put(LangID.MIDI_message_monitor,
                    new LanguageWords("MIDI Message monitor", "MIDIメッセージモニター", "MIDI信息监视器", "MIDI 信息監視器", "MIDI 메시지 모니터", "Монитор сообщений MIDI"));
            put(LangID.MIDI_message_sender,
                    new LanguageWords("MIDI Message sender", "MIDIメッセージ送信", "MIDI信息发送者", "MIDI 信息發送器", "MIDI 메시지 발신자", "Отправитель MIDI-сообщений"));
            put(LangID.Now_loading, new LanguageWords("Now loading", "ロード中", "现在加载", "正在加載", "지금 로드 중", "Сейчас загружается"));
            put(LangID.Plugin_error, new LanguageWords("Plugin error", "プラグインエラー", "插件错误", "插件錯誤", "플러그인 오류", "Ошибка плагина"));
            put(LangID.FILE_ERROR_1, new LanguageWords("※Other files cannot be loaded while loading a file.", "※ファイルロード中に他のファイルをロードできません。", "※加载文件时无法加载其他文件。",
                    "※加載文件時無法加載其他文件。", "※파일 불러오기 중에는 다른 파일을 불러올 수 없습니다.", "※ Другие файлы не могут быть загружены во время загрузки файла."));
            put(LangID.FILE_ERROR_2, new LanguageWords("※File format not supported.", "※サポート外のファイル形式です", "※不支持文件格式。", "※不支持文件格式。", "※파일 형식은 지원하지 않습니다.",
                    "※ Формат файла не поддерживается."));
            put(LangID.FILE_ERROR_3, new LanguageWords("※Files cannot be loaded during playback.", "※再生中はファイルロードできません。", "※播放过程中无法加载文件。", "※播放過程中無法加載文件。",
                    "※재생 중에는 파일을 불러올 수 없습니다.", "※ Файлы не могут быть загружены во время воспроизведения."));
            put(LangID.FILE_ERROR_4,
                    new LanguageWords("※The file cannot be opened.", "※ファイルを開くことができません。", "※文件无法打开。", "※無法打開文件。", "※파일을 열 수 없습니다.", "※ Файл не открывается."));
            put(LangID.FILE_ERROR_5, new LanguageWords("※Failed to load the file.", "※ファイルのロードに失敗しました。", "※无法加载文件。", "※加載文件失敗。", "※파일 불러오기에 실패했습니다.",
                    "※ Не удалось загрузить файл."));
            put(LangID.FILE_ERROR_6, new LanguageWords("※There is no data to play.", "※再生するデータがありません。", "※没有数据可玩。", "※沒有數據可玩。", "※재생할 데이터가 없습니다.",
                    "※ Нет данных для воспроизведения."));
            put(LangID.FILE_LOAD_SUCCESS,
                    new LanguageWords("File loading successful.", "ファイルロード成功", "文件加载成功。", "文件加載成功。", "파일 로드에 성공했습니다.", "Файл загружен успешно."));
            put(LangID.PLUGIN_LOAD_SUCCESS,
                    new LanguageWords("Plugin loading successful.", "プラグインロード成功", "插件加载成功。", "插件加載成功。", "플러그인 로드에 성공했습니다.", "Плагин загружается успешно."));
            put(LangID.PLUGIN_LOAD_ERROR,
                    new LanguageWords("Plugin loading failed.", "プラグインロード失敗", "插件加载失败。", "插件加載失敗。", "플러그인 로드에 실패했습니다.", "Ошибка загрузки плагина."));
            put(LangID.Clear, new LanguageWords("Clear", "クリア", "明确", "清除", "분명한", "Прозрачный"));
            put(LangID.Playback, new LanguageWords("Playback", "再生", "回放", "回放", "재생", "Воспроизведение"));
            put(LangID.Stop, new LanguageWords("Stop", "停止", "停止", "停止", "중지", "Останавливаться"));
            put(LangID.Fast_forward, new LanguageWords("Fast forward", "早送り", "快进", "快進", "빨리 감기", "Перемотка вперед"));
            put(LangID.Rewind, new LanguageWords("Rewind", "巻き戻し", "倒带", "倒帶", "되감기", "Перемотка назад"));
            put(LangID.Next, new LanguageWords("Next", "次へ", "下一个", "下一個", "다음", "Следующий"));
            put(LangID.Previous, new LanguageWords("Previous", "前へ", "以前", "以前的", "이전의", "Предыдущий"));
            put(LangID.Close, new LanguageWords("Close", "閉じる", "关", "關閉", "닫다", "Закрывать"));
            put(LangID.Add, new LanguageWords("Add", "追加", "加", "添加", "추가하다", "Добавлять"));
            put(LangID.Remove, new LanguageWords("Remove", "削除", "去掉", "消除", "제거하다", "Удалять"));
            put(LangID.Open_with_Explorer,
                    new LanguageWords("Open with Explorer", "エクスプローラで開く", "用资源管理器打开", "用資源管理器打開", "탐색기로 열기", "Открыть с помощью проводника"));
            put(LangID.MIDI_Data_byte, new LanguageWords("MIDI Data byte（Hex）", "MIDIデータバイト（Hex）", "MIDI数据字节（十六进制）", "MIDI 數據字節（Hex）", "MIDI 데이터 바이트(16진수)",
                    "Байт данных MIDI (шестнадцатеричный)"));
            put(LangID.Send, new LanguageWords("Send", "送信", "发送", "發送", "보내다", "Отправлять"));
            put(LangID.Invalid_byte_data,
                    new LanguageWords("Invalid byte data", "無効なバイトデータ", "无效的字节数据", "無效的字節數據", "잘못된 바이트 데이터", "Недействительные байтовые данные"));
            put(LangID.License, new LanguageWords("License", "ライセンス", "执照", "執照", "특허", "Лицензия"));
            put(LangID.Above_conditions, new LanguageWords("Above conditions：", "上記の条件に：", "以上条件：", "以上條件：", "위 조건:", "Выше условий ："));
            put(LangID.Accept, new LanguageWords("Accept", "同意する", "接受", "接受", "수용하다", "Принимать"));
            put(LangID.Reject, new LanguageWords("Reject", "同意しない", "拒绝", "拒絕", "거부하다", "Отклонять"));
            put(LangID.Copied_to_clipboard,
                    new LanguageWords("Copied to clipboard.", "クリップボードにコピーしました。", "复制到剪贴板。", "複製到剪貼板。", "클립보드에 복사했습니다.", "Скопировано в буфер обмена."));
            put(LangID.Original_copy, new LanguageWords("Original copy", "原文のコピー", "最初版", "最初版", "원본", "Оригинальная копия"));
            put(LangID.Common_settings, new LanguageWords("Common settings", "共通設定", "常用设定", "常用設置", "공통 설정", "Общие настройки"));
            put(LangID.Apply, new LanguageWords("Apply", "適用", "应用", "申請", "적용하다", "Применять"));
            put(LangID.Error, new LanguageWords("Error", "エラー", "错误", "錯誤", "오류", "Ошибка"));
            put(LangID.Message, new LanguageWords("Message", "メッセージ", "信息", "信息", "메세지", "Сообщение"));
            put(LangID.Layout_initialization, new LanguageWords("Layout initialization", "レイアウト初期化", "布局初始化", "佈局初始化", "레이아웃 초기화", "Инициализация макета"));
            put(LangID.Tool, new LanguageWords("Tool", "ツール", "工具", "工具", "도구", "Инструмент"));
            put(LangID.FFmpeg_converter, new LanguageWords("Audio file conversion(FFmpeg)", "音声ファイル変換（FFmpeg）", "音频文件转换 (FFmpeg)", "音頻文件轉換(FFmpeg)",
                    "오디오 파일 변환(FFmpeg)", "Конвертация аудиофайлов (FFmpeg)"));
            put(LangID.FFmpeg_path,
                    new LanguageWords("FFmpeg path(.exe)", "FFmpeg実行ファイル(.exe)", "FFmpeg路径（.exe）", "FFmpeg 路徑（.exe）", "FFmpeg 경로(.exe)", "Путь FFmpeg (.exe)"));
            put(LangID.Output_directory, new LanguageWords("Output directory", "出力フォルダ", "输出目录", "輸出目錄", "출력 디렉토리", "Выходной каталог"));
            put(LangID.Input_file, new LanguageWords("Input file", "入力ファイル", "输入文件", "輸入文件", "입력 파일", "Входной файл"));
            put(LangID.Play_after_convert, new LanguageWords("Play after convert", "変換後に再生する", "转换后播放", "轉換後播放", "변환 후 재생", "Играйте после конвертации"));
            put(LangID.Convert, new LanguageWords("Convert", "変換", "兑换", "兌換", "전환하다", "Конвертировать"));
            put(LangID.A_is_invalid,
                    new LanguageWords("[NAME] is invalid.", "[NAME]が無効です。", "[NAME]无效。", "[NAME] 無效。", "[NAME]이(가) 잘못되었습니다.", "[ИМЯ] недействителен."));
            put(LangID.Conversion_failed, new LanguageWords("Conversion failed.", "変換失敗。", "转换失败。", "轉換失敗。", "변환에 실패했습니다.", "Преобразование не выполнено."));
            put(LangID.Conversion_completed,
                    new LanguageWords("Conversion completed.", "変換完了。", "转换完成。", "轉換完成。", "변환이 완료되었습니다.", "Преобразование завершено."));
            put(LangID.Now_converting, new LanguageWords("Now converting.", "変換中。", "现在转换。", "現在轉換。", "이제 변환 중입니다.", "Сейчас конвертирую."));
            put(LangID.Initialize_setting, new LanguageWords("Initialize setting", "初期設定に戻す", "初始化设定", "初始化設置", "초기화 설정", "Инициализировать настройку"));
            put(LangID.Leave_output_file, new LanguageWords("Leave output file", "出力ファイルを残す", "离开输出文件", "離開輸出文件", "출력 파일 나가기", "Оставить выходной файл"));
            put(LangID.Use_FFmpeg_player, new LanguageWords("Use FFmpeg player", "FFmpegプレイヤー使用", "使用FFmpeg播放器", "使用 FFmpeg 播放器", "FFmpeg 플레이어 사용",
                    "Используйте проигрыватель FFmpeg"));
            put(LangID.Automatically_assign_MIDI_channel, new LanguageWords("Automatically assign MIDI channel", "「MIDIチャンネル」を自動的に割り当てる", "自动分配MIDI通道",
                    "自動分配 MIDI 通道", "자동으로 MIDI 채널 할당", "Автоматически назначать MIDI-канал"));
            put(LangID.Automatically_assign_Program_change_number, new LanguageWords("Automatically assign Program change number", "「プログラムチェンジ」を自動的に割り当てる",
                    "自动分配程序更改号", "自動分配程序更改編號", "프로그램 변경 번호 자동 할당", "Автоматически присвоить номер изменения программы"));
            put(LangID.Confirm_jms_with_different_versions,
                    new LanguageWords("There are plugins with different versions. Do you want to load it?", "バージョンが異なるプラグインが存在します。ロードしますか？", "有不同版本的插件。是否要加载？",
                            "有不同版本的插件。你想加載它嗎？", "버전이 다른 플러그인이 있습니다. 로드하시겠습니까?", "Есть плагины с разными версиями. Вы хотите его загрузить?"));
            put(LangID.Confirm_jmz_with_different_versions,
                    new LanguageWords("The version of the plugin you are trying to add is different. Do you want to add it?",
                            "追加しようとしているプラグインのバージョンが異なります。追加しますか？", "您尝试添加的插件版本不同。 您要添加吗？", "您嘗試添加的插件版本不同。您要添加它嗎？", "추가하려는 플러그인의 버전이 다릅니다. 추가하시겠습니까?",
                            "Версия плагина, которую вы пытаетесь добавить, отличается. Вы хотите его добавить?"));
            put(LangID.D_and_D_the_playback_file_here, new LanguageWords("Drag and drop the playback file here.", "再生ファイルをここにドラッグ&ドロップしてください。", "将播放文件拖放到此处。",
                    "將播放文件拖放到此處。", "재생 파일을 여기에 끌어다 놓습니다.", "Перетащите сюда файл воспроизведения."));
            put(LangID.Plugin_manager, new LanguageWords("Plugin manager", "プラグイン管理", "插件管理", "插件管理器", "플러그인 관리자", "Менеджер плагинов"));
            put(LangID.Lyrics_display, new LanguageWords("Lyrics display", "歌詞表示", "歌词显示", "歌詞顯示", "가사 표시", "Отображение текстов песен"));
            put(LangID.Send_system_setup_before_playback, new LanguageWords("Send system setup before playback", "再生前にシステムセットアップを送信する", "播放前发送系统设置",
                    "播放前發送系統設置", "재생 전에 시스템 설정 보내기", "Отправить настройки системы перед воспроизведением"));
            put(LangID.Whether_to_display_every_time_at_startup, new LanguageWords("Whether to display every time at startup", "起動時に毎回表示するか", "是否在每次启动时显示",
                    "是否每次啟動都顯示", "시작할 때마다 표시할지 여부", "Отображать ли каждый раз при запуске"));
            put(LangID.Play_from_the_beginning, new LanguageWords("Play from the beginning", "最初から再生", "从头开始", "從頭開始玩", "처음부터 플레이", "Играйте с самого начала"));
            put(LangID.Send_system_setup,
                    new LanguageWords("Send system setup", "システムセットアップ送信", "发送系统设置", "發送系統設置", "시스템 설정 보내기", "Отправить настройки системы"));
            put(LangID.Automatic_selection, new LanguageWords("Automatic selection", "自動選択", "自动选择", "自動選擇", "자동 선택", "Автоматический выбор"));
            put(LangID.Builtin_synthesizer,
                    new LanguageWords("Built-in synthesizer", "内蔵シンセサイザー", "Built-in synthesizer", "內置合成器", "내장 신디사이저", "Встроенный синтезатор"));
            put(LangID.Builtin_synthesizer_settings, new LanguageWords("Built-in synthesizer settings", "内蔵シンセサイザー設定", "Built-in synthesizer settings",
                    "內置合成器設置", "내장 신디사이저 설정", "Встроенные настройки синтезатора"));
            put(LangID.An_unexpected_error_has_occurred, new LanguageWords("An unexpected error has occurred.", "予期しないエラーが発生しました。", "发生意外的错误。", "發生意外的錯誤。",
                    "예상치 못한 오류가 발생했습니다.", "Произошла непредвиденная ошибка."));
            put(LangID.Exit_the_application,
                    new LanguageWords("Exit the application.", "アプリケーションを終了します。", "退出应用程序。", "退出應用程序。", "응용 프로그램을 종료합니다.", "Выйти из приложения."));
            put(LangID.There_was_a_problem_preparing_the_music_player,
                    new LanguageWords("There was a problem preparing the music player.", "音楽プレイヤーの準備に問題が発生しました。", "准备音乐播放器时出现问题。", "準備音樂播放器時出現問題。",
                            "뮤직 플레이어를 준비하는 중에 문제가 발생했습니다.", "Не удалось подготовить музыкальный проигрыватель."));
            put(LangID.Failed_to_initialize_the_application, new LanguageWords("Failed to initialize the application.", "アプリケーションの初期化処理に失敗しました。", "无法初始化应用程序。",
                    "未能初始化應用程序。", "애플리케이션을 초기화하지 못했습니다.", "Не удалось инициализировать приложение."));
            put(LangID.The_application_could_not_be_terminated_successfully, new LanguageWords("The application could not be terminated successfully.",
                    "アプリケーションを正常に終了できませんでした。", "无法成功终止该应用程序。", "無法成功終止應用程序。", "응용 프로그램을 성공적으로 종료할 수 없습니다.", "Приложение не может быть успешно завершено."));
            put(LangID.Dont_choose_a_synthesizer,
                    new LanguageWords("Don't choose a synthesizer", "シンセサイザーを選択しない", "不要选择合成器", "不要選擇合成器", "신디사이저를 선택하지 마십시오", "Не выбирайте синтезатор"));
            put(LangID.Random_playback, new LanguageWords("Random playback", "ランダム再生", "随机播放", "隨機播放", "랜덤 재생", "Воспроизведение в случайном порядке"));
            put(LangID.Allow_window_size_change,
                    new LanguageWords("Allow window size change", "Windowサイズの変更を許可", "允许更改窗口大小", "允許更改窗口大小", "창 크기 변경 허용", "Разрешить изменение размера окна"));
            put(LangID.Load, new LanguageWords("Load", "読み込み", "Load", "Load", "Load", "Load"));
            put(LangID.Midi_File, new LanguageWords("MIDI File", "MIDIファイル", "MIDI File", "MIDI File", "MIDI File", "MIDI File"));
            put(LangID.Wav_File, new LanguageWords("WAV File", "WAVファイル", "WAV File", "WAV File", "WAV File", "WAV File"));
            put(LangID.Synchronized_MIDI_and_WAV_play, new LanguageWords("Synchronized MIDI and WAV play", "MIDI/WAV同期再生", "Synchronized MIDI and WAV play",
                    "Synchronized MIDI and WAV play", "Synchronized MIDI and WAV play", "Synchronized MIDI and WAV play"));
        }
    };
}
