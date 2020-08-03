package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.net.URL;

@Mod(modid = ExampleMod.MODID, version = ExampleMod.VERSION)
public class ExampleMod
{
    public static final String MODID = "antennae";
    public static final String VERSION = "1.0";
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        if(event.getSide()== Side.CLIENT){

            MinecraftForge.EVENT_BUS.register(this);
        }

    }

    private static String done = "";

    @SubscribeEvent
    public void renderPlayer(RenderPlayerEvent.Post event)
    {
        EntityPlayer player = event.entityPlayer;

        if(done.contains(player.getName()))
            return;

        String uuid = player.getUniqueID().toString().replace("-","");

        if (player instanceof AbstractClientPlayer)
        {
            AbstractClientPlayer acp = (AbstractClientPlayer) player;

            if (acp.hasPlayerInfo())
            {
                NetworkPlayerInfo playerInfo = ObfuscationReflectionHelper.getPrivateValue(AbstractClientPlayer.class, acp, "field_175157_a");
                ResourceLocation cape = new ResourceLocation(MODID, String.format("capes/%s", uuid));
                System.out.print(String.format("capes/%s", uuid)+"\n");

                if(loadCape(cape, uuid))
                    ObfuscationReflectionHelper.setPrivateValue(NetworkPlayerInfo.class, playerInfo, cape, "locationCape");

                done+=player.getName();
            }
        }
    }

    private static final String url = "http://121.37.27.192:233/capes/%s.png";

    public boolean loadCape(ResourceLocation resourceLocation, String uuid)
    {
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        ITextureObject iTextureObject = new CustomTexture(resourceLocation, String.format(url, uuid));
        return textureManager.loadTexture(resourceLocation, iTextureObject);
    }

    public class CustomTexture extends SimpleTexture {

        private final String url;
        private BufferedImage image;

        public CustomTexture(ResourceLocation textureResourceLocation, String url) {
            super(textureResourceLocation);
            this.url=url;
        }

        @Override
        public void loadTexture(IResourceManager resourceManager) throws IOException {

            if(image==null) {

                try {
                    this.image = ImageIO.read(new URL(url));
                    if (this.image != null) {
                        loadTexture(resourceManager);
                    }

                } catch (IOException e) {
                    throw e;
                }
            }
            TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), image, false,true);
        }
    }
}