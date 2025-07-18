package LLDQuestions.InMemoryFileSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Main class to demonstrate the File System API.
 */
public class FileSystemDemo {
    public static void main(String[] args) {
        FileSystem fs = new FileSystem();

        System.out.println("----- Initial State -----");
        System.out.println("Current Path: " + fs.pwd()); // Should be "/"
        System.out.println();

        System.out.println("----- Creating Directories -----");
        fs.mkdir("/home");
        fs.mkdir("/home/user");
        fs.mkdir("/home/guest");
        fs.mkdir("/temp");
        System.out.println("Listing root contents:");
        fs.ls("/"); // Should show home, temp
        System.out.println();

        System.out.println("----- Creating Files -----");
        fs.touch("/home/user/file1.txt");
        fs.touch("/home/user/file2.log");
        System.out.println("Listing /home/user contents:");
        fs.ls("/home/user"); // Should show file1.txt, file2.log
        System.out.println();

        System.out.println("----- Changing Directory -----");
        fs.cd("/home/user");
        System.out.println("Current Path: " + fs.pwd()); // Should be "/home/user"
        System.out.println("Listing current directory contents (ls .):");
        fs.ls("."); // Should show file1.txt, file2.log
        System.out.println();

        System.out.println("----- Navigating with '..' -----");
        fs.cd("..");
        System.out.println("Current Path: " + fs.pwd()); // Should be "/home"
        System.out.println("Listing /home contents:");
        fs.ls("."); // Should show user, guest
        System.out.println();
    }
}

// The Component: A common interface for both Files (Leaf) and Directories (Composite)
abstract class FileSystemEntry {
    protected String name;
    protected Directory parent;

    public FileSystemEntry(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Directory getParent() {
        return parent;
    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }

    public String getFullPath() {
        if (parent == null) { // This is the root directory
            return name;
        }
        // To avoid double slashes for root's children
        if (parent.getParent() == null) {
            return parent.getFullPath() + name;
        }
        return parent.getFullPath() + "/" + name;
    }
}

// The Leaf: Represents a single file
class File extends FileSystemEntry {
    public File(String name) {
        super(name);
    }
}

// The Composite: Represents a directory that can contain other entries
class Directory extends FileSystemEntry {
    private List<FileSystemEntry> children = new ArrayList<>();

    public Directory(String name) {
        super(name);
    }

    public List<FileSystemEntry> getChildren() {
        return children;
    }

    public void addEntry(FileSystemEntry entry) {
        entry.setParent(this);
        children.add(entry);
    }

    public FileSystemEntry getChild(String name) {
        for (FileSystemEntry child : children) {
            if (child.getName().equals(name)) {
                return child;
            }
        }
        return null;
    }
}

// The Client: Manages the file system state and commands
class FileSystem {
    private final Directory root;
    private Directory currentDirectory;

    public FileSystem() {
        this.root = new Directory("/");
        this.currentDirectory = this.root;
    }

    public String pwd() {
        return currentDirectory.getFullPath();
    }

    public void mkdir(String path) {
        createEntry(path, true);
    }

    public void touch(String path) {
        createEntry(path, false);
    }

    private void createEntry(String path, boolean isDirectory) {
        String[] parts = path.split("/");
        String newEntryName = parts[parts.length - 1];
        String parentPath = path.substring(0, path.lastIndexOf('/'));
        if (parentPath.isEmpty()) parentPath = "/";

        Directory parentDir = findDirectory(parentPath);
        if (parentDir != null) {
            FileSystemEntry newEntry = isDirectory ? new Directory(newEntryName) : new File(newEntryName);
            parentDir.addEntry(newEntry);
        } else {
            System.out.println("Error: Cannot create. Path not found: " + parentPath);
        }
    }

    public void ls(String path) {
        Directory dir = findDirectory(path);
        if (dir != null) {
            List<String> names = new ArrayList<>();
            for (FileSystemEntry entry : dir.getChildren()) {
                names.add(entry.getName());
            }
            Collections.sort(names);
            for (String name : names) {
                System.out.println("  " + name);
            }
        } else {
            System.out.println("Error: Directory not found: " + path);
        }
    }

    public void cd(String path) {
        Directory targetDir = findDirectory(path);
        if (targetDir != null) {
            currentDirectory = targetDir;
        } else {
            System.out.println("Error: Directory not found: " + path);
        }
    }

    private Directory findDirectory(String path) {
        if (path.equals("/")) return root;
        
        Directory startDir = path.startsWith("/") ? root : currentDirectory;
        String[] parts = path.split("/");
        
        Directory current = startDir;
        for (String part : parts) {
            if (part.isEmpty() || part.equals(".")) continue;
            if (part.equals("..")) {
                current = current.getParent();
                if (current == null) return null; // Can't go above root
                continue;
            }

            FileSystemEntry entry = current.getChild(part);
            if (entry instanceof Directory) {
                current = (Directory) entry;
            } else {
                return null; // Path component is not a directory
            }
        }
        return current;
    }
}

