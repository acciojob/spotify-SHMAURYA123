package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user=new User(name,mobile);
        users.add(user);
        List<Playlist>playlistList=new ArrayList<>();
        userPlaylistMap.put(user,playlistList);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist=new Artist(name);
        artists.add(artist);
        List<Album> albumList=new ArrayList<>();
        artistAlbumMap.put(artist,albumList);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Artist artist=null;
        for(Artist a:artists){
            if(a.getName().equals(artistName)){
                artist=a;
                break;
            }
        }

        if(artist==null){
            artist=createArtist(artistName);
        }
        Album album=new Album(title);
        albums.add(album);
        artistAlbumMap.get(artist).add(album);
        albumSongMap.put(album,new ArrayList<>());
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        Album album=null;
        for (Album a:albums){
            if(a.getTitle().equals(albumName)){
                album=a;
                break;
            }
        }

        if(album==null){
            throw new Exception("Album does not exist");
        }

        Song song=new Song(title,length);
        songs.add(song);
        song.setLikes(0);
        albumSongMap.put(album,new ArrayList<>());
        songLikeMap.put(song,new ArrayList<>());
        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        User user=null;
        for(User s:users){
            if(s.getMobile().equals(mobile)){
                user=s;
                break;
            }
        }


        if(user==null){
            throw new Exception("User does not exist");
        }

        Playlist playlist=new Playlist(title);
        playlists.add(playlist);

        playlistListenerMap.put(playlist,new ArrayList<>());
        playlistSongMap.put(playlist,new ArrayList<>());

        for(Song song:songs){
            if(song.getLength()==length){
                playlistSongMap.get(playlist).add(song);
            }
        }

        playlistListenerMap.get(playlist).add(user);
        creatorPlaylistMap.put(user,playlist);
        userPlaylistMap.get(user).add(playlist);

        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        User user=null;
        for(User s:users){
            if(s.getMobile().equals(mobile)){
                user=s;
                break;
            }
        }


        if(user==null){
            throw new Exception("User does not exist");
        }

        Playlist playlist=new Playlist(title);
        playlists.add(playlist);

        playlistListenerMap.put(playlist,new ArrayList<>());
        playlistSongMap.put(playlist,new ArrayList<>());

        for(Song song:songs){
            if(songTitles.contains(song.getTitle())){
                playlistSongMap.get(playlist).add(song);
            }
        }
        playlistListenerMap.get(playlist).add(user);
        userPlaylistMap.get(user).add(playlist);
        creatorPlaylistMap.put(user,playlist);

        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User user=null;
        for(User s:users){
            if(s.getMobile().equals(mobile)){
                user=s;
                break;
            }
        }


        if(user==null){
            throw new Exception("User does not exist");
        }

        Playlist playlist=null;
        for(Playlist p:playlists){
            if(p.getTitle().equals(playlistTitle)){
                playlist=p;
                break;
            }
        }
        if(playlist==null){
            throw new Exception("Playlist does not exist");
        }

        if(creatorPlaylistMap.containsKey(user)&&playlistListenerMap.get(playlist).contains(user)||creatorPlaylistMap.get(user)==playlist){
            return playlist;
        }

        playlistListenerMap.get(playlist).add(user);

        if(!userPlaylistMap.get(user).contains(playlist)){
            userPlaylistMap.get(user).add(playlist);
        }
        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user=null;
        for(User user1:users){
            if(user1.getMobile().equals(mobile)){
                user=user1;
                break;
            }
        }
        if(user == null)
            throw new Exception("User does not exist");

        Song song=null;
        for(Song song1:songs){
            if(song1.getTitle().equals(songTitle)){
                song=song1;
                break;
            }
        }
        if(song == null)
            throw new Exception("Song does not exist");

        if(songLikeMap.get(song).contains(user)){
            return song;
        }
        song.setLikes(song.getLikes()+1);
        songLikeMap.get(song).add(user);

        for(Album album:albumSongMap.keySet()){
            if(albumSongMap.get(album).contains(song)){
                for(Artist artist:artistAlbumMap.keySet()){
                    if(artistAlbumMap.get(artist).contains(album)){
                        artist.setLikes(artist.getLikes()+1);
                        break;
                    }
                }
                break;
            }
        }
        return song;
    }

    public String mostPopularArtist() {
        int countLikes=Integer.MIN_VALUE;
        String popularArtist="";
        for(Artist artist:artists){
            if(artist.getLikes() > countLikes){
                popularArtist=artist.getName();
                countLikes=artist.getLikes();
            }
        }
        return popularArtist;
    }

    public String mostPopularSong() {
        int countLikes=Integer.MIN_VALUE;
        String popularSong="";
        for(Song song:songs){
            if(song.getLikes() > countLikes){
                popularSong=song.getTitle();
                countLikes=song.getLikes();
            }
        }
        return popularSong;
    }
}
