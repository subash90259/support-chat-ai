package com.example.support_chat_ai.tool;

import java.awt.Desktop;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class FileSystemTool {
    private Path currentFolder;

    private Path resolvePath(String location) {

    String userHome = System.getProperty("user.home");

    String normalizedLocation = location
            .trim()
            .toLowerCase()
            .replace(" folder", "");

    System.out.println("Original location   = " + location);
    System.out.println("Normalized location = " + normalizedLocation);

    switch (normalizedLocation) {

        case "desktop":
            return getWindowsKnownFolder(
                    "Desktop",
                    Path.of(userHome, "Desktop")
            );

        case "documents":
        case "document":
            return getWindowsKnownFolder(
                    "MyDocuments",
                    Path.of(userHome, "Documents")
            );

        case "pictures":
        case "picture":
            return getWindowsKnownFolder(
                    "MyPictures",
                    Path.of(userHome, "Pictures")
            );

        case "music":
            return getWindowsKnownFolder(
                    "MyMusic",
                    Path.of(userHome, "Music")
            );

        case "videos":
        case "video":
            return getWindowsKnownFolder(
                    "MyVideos",
                    Path.of(userHome, "Videos")
            );

        case "downloads":
        case "download":
            return Path.of(userHome, "Downloads");

        default:
            return Path.of(location);
    }
}
private Path getWindowsKnownFolder(
        String folderName,
        Path fallbackPath) {

    try {

        String command =
                "[Environment]::GetFolderPath('" + folderName + "')";

        Process process = new ProcessBuilder(
                "powershell.exe",
                "-NoProfile",
                "-Command",
                command
        )
                .redirectErrorStream(true)
                .start();

        String result = new String(
                process.getInputStream().readAllBytes()
        ).trim();

        process.waitFor();

        if (!result.isBlank()) {

            Path actualPath = Path.of(result);

            System.out.println(
                    "Windows known folder "
                    + folderName
                    + " = "
                    + actualPath
            );

            if (Files.exists(actualPath)) {
                return actualPath;
            }
        }

    } catch (Exception e) {

        System.out.println(
                "Known folder lookup failed: "
                + e.getMessage()
        );
    }

    System.out.println(
            "Using fallback path = " + fallbackPath
    );

    return fallbackPath;
}


    @Tool(description = """
Check whether a file or folder exists on the user's computer.

Use this tool ONLY when the user explicitly asks whether
a file or folder exists.

Examples:

"Does the Java folder exist?"
"Check whether test.txt exists"
"Is there a Downloads folder?"

Do NOT use this tool for normal questions.

"What is Java?"
"What is a folder?"
"Explain Java"

These must be answered normally without using this tool.
""")
    public String checkPathExists(String location) {

        Path path = resolvePath(location);
       


        if (Files.exists(path)) {
            return "Path exists: " + path;
        }

        return "Path does not exist: " + path;
    }
    @Tool(description = """
Find a folder inside a specified parent folder.

IMPORTANT:
Use this tool ONLY when the user explicitly asks to find,
locate, search for, or check a folder on the computer.

Do NOT use this tool for normal questions or explanations.

For example:

"Find the Java folder inside Downloads"
"Locate the project folder"
"Check if the Java folder exists"

Do NOT use this tool for:

"What is Java?"
"Explain Java"
"What is Spring Boot?"

Those are normal questions and must be answered directly.
""")
public String findFolder(String parentFolder, String folderName) {

    try {

        Path parentPath = resolvePath(parentFolder);

        System.out.println("FIND FOLDER TOOL CALLED");
        System.out.println("Parent folder = " + parentPath);
        System.out.println("Folder name   = " + folderName);

        if (!Files.exists(parentPath)) {
            return "PARENT_FOLDER_NOT_FOUND: " + parentPath;
        }

        try (var paths = Files.list(parentPath)) {

            var match = paths
                    .filter(Files::isDirectory)
                    .filter(path ->
                            path.getFileName()
                                    .toString()
                                    .equalsIgnoreCase(folderName)
                    )
                    .findFirst();

            if (match.isPresent()) {

                Path foundPath = match.get().toAbsolutePath();
  this.currentFolder = foundPath;

    System.out.println("Found folder   = " + foundPath);
    System.out.println("Current folder = " + currentFolder);

                

                return "FOLDER_FOUND: " + foundPath;
            }

            return "FOLDER_NOT_FOUND: "
                    + folderName
                    + " inside "
                    + parentPath;
        }

    } catch (Exception e) {

        e.printStackTrace();

        return "FOLDER_SEARCH_FAILED: " + e.getMessage();
    }
}


    @Tool(description = """
    Open a folder in Windows File Explorer.
    Use ONLY when the user explicitly asks to
    open, show, or go to the folder.
    Do NOT use this tool for existence questions.
    """)

public String openFolder(String location) {

    try {

        System.out.println("OPEN FOLDER TOOL CALLED");
        System.out.println("Received location = " + location);

        Path path = resolvePath(location);

        System.out.println("Resolved path = " + path);
        System.out.println("Exists = " + Files.exists(path));

        if (!Files.exists(path)) {
            return "FOLDER_NOT_FOUND: " + path;
        }

        if (!Files.isDirectory(path)) {
            return "NOT_A_FOLDER: " + path;
        }

        new ProcessBuilder(
                "explorer.exe",
                path.toAbsolutePath().toString()
        ).start();

        return "FOLDER_OPENED_SUCCESSFULLY: " + path;

    } catch (Exception e) {

        e.printStackTrace();

        return "FOLDER_OPEN_FAILED: "
                + e.getClass().getSimpleName()
                + " - "
                + e.getMessage();
    }
}
@Tool(description = """
        Close an already opened folder window in Windows File Explorer.
        Use this tool ONLY when the user explicitly asks to close a folder.

        Examples:
        'Documents close pannu'
        'Downloads folder close pannu'
        'close Pictures'

        If the user only says 'close', use the previous conversation
        context to determine which folder they mean.
        """)
public String closeFolder(String location) {

    try {

        Path path = resolvePath(location);

        System.out.println("CLOSE FOLDER TOOL CALLED");
        System.out.println("Location = " + location);
        System.out.println("Resolved path = " + path);

        String targetPath = path
                .toAbsolutePath()
                .toString()
                .replace("'", "''");

        String script =
                "$shell = New-Object -ComObject Shell.Application; " +
                "$windows = @($shell.Windows()); " +
                "$target = '" + targetPath + "'; " +
                "$closed = $false; " +
                "foreach ($window in $windows) { " +
                "  try { " +
                "    $folderPath = $window.Document.Folder.Self.Path; " +
                "    if ($folderPath -ieq $target) { " +
                "      $window.Quit(); " +
                "      $closed = $true; " +
                "    } " +
                "  } catch {} " +
                "}; " +
                "if ($closed) { Write-Output 'CLOSED' } " +
                "else { Write-Output 'NOT_OPEN' }";

        Process process = new ProcessBuilder(
                "powershell.exe",
                "-NoProfile",
                "-Command",
                script
        )
                .redirectErrorStream(true)
                .start();

        String result = new String(
                process.getInputStream().readAllBytes()
        ).trim();

        process.waitFor();

        if (result.contains("CLOSED")) {
            return "FOLDER_CLOSED_SUCCESSFULLY: " + path;
        }

        return "FOLDER_NOT_OPEN: " + path;

    } catch (Exception e) {

        e.printStackTrace();

        return "FOLDER_CLOSE_FAILED: " + e.getMessage();
    }
}
@Tool(description = """
Copy the currently selected folder to a destination.

Use this tool ONLY when the user explicitly asks
to copy a folder or file.

Examples:

"Copy this folder to Documents"
"Copy the Java project to Desktop"

Do NOT use this tool for normal conversation.
""")
public String copyCurrentFolder(String destination) {

    try {

        if (currentFolder == null) {
            return "NO_CURRENT_FOLDER: Find or select a folder first.";
        }

        if (!Files.exists(currentFolder)) {
            return "CURRENT_FOLDER_NOT_FOUND: " + currentFolder;
        }

        if (!Files.isDirectory(currentFolder)) {
            return "CURRENT_PATH_IS_NOT_FOLDER: " + currentFolder;
        }

        Path destinationPath = resolvePath(destination);

        if (!Files.exists(destinationPath)) {
            return "DESTINATION_NOT_FOUND: " + destinationPath;
        }

        if (!Files.isDirectory(destinationPath)) {
            return "DESTINATION_IS_NOT_FOLDER: " + destinationPath;
        }

        Path targetFolder =
                destinationPath.resolve(currentFolder.getFileName());

        System.out.println("COPY CURRENT FOLDER TOOL CALLED");
        System.out.println("Current folder = " + currentFolder);
        System.out.println("Destination    = " + destinationPath);
        System.out.println("Target         = " + targetFolder);

        if (Files.exists(targetFolder)) {
            return "DESTINATION_FOLDER_ALREADY_EXISTS: "
                    + targetFolder;
        }

        try (var paths = Files.walk(currentFolder)) {

            paths.forEach(sourcePath -> {

                try {

                    Path relativePath =
                            currentFolder.relativize(sourcePath);

                    Path targetPath =
                            targetFolder.resolve(relativePath);

                    if (Files.isDirectory(sourcePath)) {

                        Files.createDirectories(targetPath);

                    } else {

                        Files.copy(sourcePath, targetPath);
                    }

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        return "FOLDER_COPIED_SUCCESSFULLY: "
                + currentFolder
                + " -> "
                + targetFolder;

    } catch (Exception e) {

        e.printStackTrace();

        return "FOLDER_COPY_FAILED: "
                + e.getClass().getSimpleName()
                + " - "
                + e.getMessage();
    }
}
}